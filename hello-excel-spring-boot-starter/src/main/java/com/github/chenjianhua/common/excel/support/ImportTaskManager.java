package com.github.chenjianhua.common.excel.support;

import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskMeta;
import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import com.github.chenjianhua.common.excel.service.ExcelServerRequestService;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.chenjianhua.common.excel.util.ApplicationContextUtil;
import com.github.chenjianhua.common.excel.util.ExcelUploadUtil;
import com.github.chenjianhua.common.excel.util.ThreadPoolUtil;
import com.github.chenjianhua.common.excel.util.UuidUtil;
import com.github.chenjianhua.common.excel.vo.ImportCallback;
import com.szkunton.common.ktcommon.exception.BusinessException;
import com.szkunton.common.ktcommon.vo.ResponseStatus;
import com.szkunton.common.ktjson.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.*;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Component
public class ImportTaskManager implements ApplicationRunner {
    private static final CompletionService<ResponseStatus<ImportCallback>> importExcelCompletionService = new ExecutorCompletionService<>(ThreadPoolUtil.getInstance());

    /**
     * 同时支持10个导出
     */
    private static Semaphore semaphore = new Semaphore(10);

    private static ImportCallback checkExcelImportParam(ImportTaskMeta taskMeta) {
        if (StringUtils.isEmpty(taskMeta.getImportCode())) {
            throw new BusinessException("导入类型为空!");
        }
        ExcelImportStrategy strategy = ExcelStrategySelector.getImportStrategy(taskMeta.getImportCode());
        if (null == strategy) {
            StringBuilder sb = new StringBuilder();
            sb.append("导入任务类型:").append(taskMeta.getImportCode()).append("没有执行策略");
            throw new BusinessException(sb.toString());
        }
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        // 创建导出任务
        ResponseStatus responseStatus = excelServerRequestService.addImportHis(taskMeta);
        if (!responseStatus.isSuccess()) {
            log.info("创建导入记录失败 :{}", JsonUtils.toJSONString(responseStatus));
            throw new BusinessException("创建导入记录失败");
        }
        return null;
    }

    public static ResponseStatus<ImportCallback> excelImport(ImportTaskMeta taskMeta) {
        taskMeta.setTaskNumber(UuidUtil.getUuid32());
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        try {
            checkExcelImportParam(taskMeta);
        } catch (BusinessException e) {
            log.info(e.getMessage());
            return ResponseStatus.error(e.getMessage());
        }
        ImportCallback importCallback = new ImportCallback();
        try {
            // 上传的导入文件保存到本地temp文件和上传到oss
            ExcelUploadUtil.handleUploadFile(taskMeta);
            importCallback = new ImportCallback();
            importCallback.setTaskNumber(taskMeta.getTaskNumber());
            semaphore.acquire();
            // 如果是异步导出任务或者当前正在执行的导出任务达到最大值
            if (!taskMeta.isSyncTask()) {
                // 异步执行，不返回文件路径
                importExcelCompletionService.submit(() -> ExcelProcessor.importExcel(taskMeta));
                return ResponseStatus.ok(importCallback);
            } else {
                return ExcelProcessor.importExcel(taskMeta);
            }
        } catch (Exception e) {
            log.error("[{}]导出失败", taskMeta.getTaskNumber(), e);
            excelServerRequestService.updateImportErrorResult(taskMeta, "上传文件异常");
            importCallback.setImportStatus(ExcelExportStatusEnum.FAIL);
            return ResponseStatus.error(500, "导入失败!", importCallback);
        } finally {
            // 只释放同步任务信号量，异步任务在异步完成时释放
            if (taskMeta.isSyncTask()) {
                semaphore.release();
            }
        }
    }

    private void handleAsyncTask() {
        while (true) {
            try {
                Future<ResponseStatus<ImportCallback>> future = importExcelCompletionService.take();
                try {
                    ResponseStatus<ImportCallback> importResp = future.get();
                } catch (ExecutionException e) {
                    log.error("导入发生异常", e);
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("导入发生异常", e);
            } finally {
                semaphore.release();
            }
        }
    }

    @Async
    @Override
    public void run(ApplicationArguments args) {
        handleAsyncTask();
    }
}
