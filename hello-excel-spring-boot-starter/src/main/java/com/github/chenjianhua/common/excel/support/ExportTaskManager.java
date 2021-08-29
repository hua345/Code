package com.github.chenjianhua.common.excel.support;

import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskParam;
import com.github.chenjianhua.common.excel.service.ExcelServerRequestService;
import com.github.chenjianhua.common.excel.util.ThreadPoolUtil;
import com.github.chenjianhua.common.excel.util.UuidUtil;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportResultVo;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import com.github.common.resp.ResponseVO;
import com.github.chenjianhua.common.excel.support.ept.ExcelExportStrategy;
import com.github.chenjianhua.common.excel.util.ApplicationContextUtil;
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
public class ExportTaskManager implements ApplicationRunner {
    private static final CompletionService<ResponseVO<ExportResultVo>> exportExcelCompletionService = new ExecutorCompletionService<>(ThreadPoolUtil.getInstance());

    /**
     * 同时支持10个导出
     */
    private static Semaphore semaphore = new Semaphore(10);

    private static ExportResultVo checkExcelExportParam(ExportTaskParam taskMeta) {
        if (!StringUtils.hasText(taskMeta.getExportCode())) {
            throw new BusinessException("导出类型为空!");
        }
        ExcelExportStrategy strategy = ExcelStrategySelector.getExportStrategy(taskMeta.getExportCode());
        if (null == strategy) {
            StringBuilder sb = new StringBuilder();
            sb.append("导出任务类型:").append(taskMeta.getExportCode()).append("没有执行策略");
            log.error(sb.toString());
            throw new BusinessException(sb.toString());
        }
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        // 创建导出任务
        ResponseVO responseStatus = excelServerRequestService.addExportHis(taskMeta);
        if (!responseStatus.isSuccess()) {
            log.info("创建导出记录失败 :{}", JsonUtil.toJsonString(responseStatus));
            throw new BusinessException("创建导出记录失败");
        }
        return null;
    }


    public static ResponseVO<ExportResultVo> excelExport(ExportTaskParam taskMeta) {
        taskMeta.setTaskNumber(UuidUtil.getUuid32());
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        try {
            checkExcelExportParam(taskMeta);
        } catch (BusinessException e) {
            log.info(e.getMessage());
            return ResponseVO.fail(e.getMessage(), null);
        }
        ExportResultVo exportResultVo = new ExportResultVo();
        try {
            exportResultVo.setTaskNumber(taskMeta.getTaskNumber());
            semaphore.acquire();
            // 如果是异步导出任务或者当前正在执行的导出任务达到最大值
            if (!taskMeta.isSyncTask()) {
                // 异步执行，不返回文件路径
                exportExcelCompletionService.submit(() -> ExcelProcessor.exportExcel(taskMeta));
                return ResponseVO.ok(exportResultVo);
            } else {
                return ExcelProcessor.exportExcel(taskMeta);
            }
        } catch (Exception e) {
            log.error("[{}]导出失败", taskMeta.getTaskNumber(), e);
            excelServerRequestService.updateExportErrorResult(taskMeta, "上传文件异常");
            return ResponseVO.fail("导出失败!", exportResultVo);
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
                Future<ResponseVO<ExportResultVo>> future = exportExcelCompletionService.take();
                try {
                    ResponseVO<ExportResultVo> callback = future.get();
                } catch (ExecutionException e) {
                    log.error("获取导出结果异常", e);
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("发生异常", e);
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
