package com.github.chenjianhua.common.excel.support;

import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskParam;
import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import com.github.chenjianhua.common.excel.service.ExcelServerRequestService;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.chenjianhua.common.excel.util.ApplicationContextUtil;
import com.github.chenjianhua.common.excel.util.ExcelUploadUtil;
import com.github.chenjianhua.common.excel.util.ThreadPoolUtil;
import com.github.chenjianhua.common.excel.util.UuidUtil;
import com.github.chenjianhua.common.excel.vo.ImportCallback;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import com.github.common.resp.ResponseVO;
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
    private static final CompletionService<ResponseVO<ImportCallback>> importExcelCompletionService = new ExecutorCompletionService<>(ThreadPoolUtil.getInstance());

    /**
     * 同时支持10个导出
     */
    private static Semaphore semaphore = new Semaphore(10);

    private static ImportCallback checkExcelImportParam(ImportTaskParam importTaskParam) {
        if (!StringUtils.hasText(importTaskParam.getImportCode())) {
            throw new BusinessException("导入类型为空!");
        }
        ExcelImportStrategy strategy = ExcelStrategySelector.getImportStrategy(importTaskParam.getImportCode());
        if (null == strategy) {
            StringBuilder sb = new StringBuilder();
            sb.append("导入任务类型:").append(importTaskParam.getImportCode()).append("没有执行策略");
            throw new BusinessException(sb.toString());
        }
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        // 创建导出任务
        ResponseVO responseStatus = excelServerRequestService.addImportHis(importTaskParam);
        if (!responseStatus.isSuccess()) {
            log.info("创建导入记录失败 :{}", JsonUtil.toJsonString(responseStatus));
            throw new BusinessException("创建导入记录失败");
        }
        return null;
    }

    public static ResponseVO<ImportCallback> excelImport(ImportTaskParam importTaskParam) {
        importTaskParam.setTaskNumber(UuidUtil.getUuid32());
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        try {
            checkExcelImportParam(importTaskParam);
        } catch (BusinessException e) {
            log.info(e.getMessage());
            return ResponseVO.fail(e.getMessage());
        }
        ImportCallback importCallback = new ImportCallback();
        try {
            // 上传的导入文件保存到本地temp文件和上传到oss
            ExcelUploadUtil.handleUploadFile(importTaskParam);
            importCallback = new ImportCallback();
            importCallback.setTaskNumber(importTaskParam.getTaskNumber());
            semaphore.acquire();
            // 如果是异步导出任务或者当前正在执行的导出任务达到最大值
            if (!importTaskParam.isSyncTask()) {
                // 异步执行，不返回文件路径
                importExcelCompletionService.submit(() -> ExcelProcessor.importExcel(importTaskParam));
                return ResponseVO.ok(importCallback);
            } else {
                return ExcelProcessor.importExcel(importTaskParam);
            }
        } catch (Exception e) {
            log.error("[{}]导出失败", importTaskParam.getTaskNumber(), e);
            excelServerRequestService.updateImportErrorResult(importTaskParam, "上传文件异常");
            importCallback.setImportStatus(ExcelExportStatusEnum.FAIL);
            return ResponseVO.fail("导入失败!", importCallback);
        } finally {
            // 只释放同步任务信号量，异步任务在异步完成时释放
            if (importTaskParam.isSyncTask()) {
                semaphore.release();
            }
        }
    }

    private void handleAsyncTask() {
        while (true) {
            try {
                Future<ResponseVO<ImportCallback>> future = importExcelCompletionService.take();
                try {
                    ResponseVO<ImportCallback> importResp = future.get();
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
