package com.github.chenjianhua.common.excel.support;

import com.github.chenjianhua.common.excel.bo.FileUploadResponse;
import com.github.chenjianhua.common.excel.bo.ept.ExportTaskMeta;
import com.github.chenjianhua.common.excel.bo.ept.ExportedMeta;
import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskMeta;
import com.github.chenjianhua.common.excel.bo.ipt.ImportedMeta;
import com.github.chenjianhua.common.excel.enums.ExcelConstants;
import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import com.github.chenjianhua.common.excel.service.ExcelServerRequestService;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.chenjianhua.common.excel.util.ApplicationContextUtil;
import com.github.chenjianhua.common.excel.util.ExcelUploadUtil;
import com.github.chenjianhua.common.excel.vo.ExportCallback;
import com.github.chenjianhua.common.excel.vo.ImportCallback;
import com.github.chenjianhua.common.excel.vo.UpdateExportHisResultParam;
import com.szkunton.common.ktcommon.exception.BusinessException;
import com.szkunton.common.ktcommon.vo.ResponseStatus;
import com.github.chenjianhua.common.excel.support.ept.ExcelExportStrategy;
import com.github.chenjianhua.common.excel.vo.UpdateImportHisResultParam;
import com.szkunton.common.ktjson.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Component
public class ExcelProcessor {

    /**
     * 导出
     *
     * @return 返回经过文件路径
     */
    public static ResponseStatus<ImportCallback> importExcel(ImportTaskMeta taskMeta) {
        ImportCallback importCallback = new ImportCallback();
        importCallback.setTaskNumber(taskMeta.getTaskNumber());
        ExcelImportStrategy strategy = ExcelStrategySelector.getImportStrategy(taskMeta.getImportCode());
        // 更新导入任务状态
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        UpdateImportHisResultParam updateImportHisResultParam = new UpdateImportHisResultParam();
        updateImportHisResultParam.setTaskNumber(taskMeta.getTaskNumber());
        updateImportHisResultParam.setImportStatus(ExcelExportStatusEnum.DOING);
        updateImportHisResultParam.setResultMsg(ExcelExportStatusEnum.DOING.getDescription());
        updateImportHisResultParam.setFileName(taskMeta.getUploadOriginTempFile().getName());
        ImportedMeta importedMeta = null;
        try {
            importedMeta = strategy.doImport(taskMeta);
            log.info("[{}]导入结果{}", taskMeta.getTaskNumber(), JsonUtils.toJSONString(importedMeta));
            FileUploadResponse uploadResponse = null;
            if (Objects.nonNull(importedMeta)) {
                uploadResponse = ExcelUploadUtil.uploadImport(importedMeta.getResultTempFile());
                log.info("[{}]上传结果{}", taskMeta.getTaskNumber(), JsonUtils.toJSONString(uploadResponse));
            }
            // 检查导出文件上传状态
            if (null == uploadResponse || ExcelConstants.RESP_SUCCESS_STATUS != uploadResponse.getCode()) {
                throw new BusinessException("上传导入结果文件异常");
            }
            importCallback.setFileName(updateImportHisResultParam.getFileName());
            importCallback.setTotalRecord(importedMeta.getTotalRecord());
            importCallback.setSuccessRecord(importedMeta.getSuccessRecord());
            importCallback.setFailedRecord(importedMeta.getFailedRecord());
            importCallback.setOriginOssFilePath(taskMeta.getImportOssFilePath());
            importCallback.setResultOssFilePath(uploadResponse.getUrl());
            importCallback.setImportStatus(ExcelExportStatusEnum.SUCCESS);
            // 导出成功更新导出结果
            excelServerRequestService.updateImportSuccessResult(taskMeta, importedMeta, uploadResponse);
            log.info("[{}]导入任务完成", taskMeta.getTaskNumber());
        } catch (BusinessException e) {
            excelServerRequestService.updateImportErrorResult(taskMeta, e.getMessage());
            log.error("[{}]导入失败", taskMeta.getTaskNumber(), e);
            return ResponseStatus.error(ExcelConstants.RESP_FAIL_STATUS, "导出任务处理失败," + e.getMessage(), importCallback);
        } catch (Exception e) {
            excelServerRequestService.updateImportErrorResult(taskMeta, null);
            log.error("[{}]导入失败", taskMeta.getTaskNumber(), e);
            return ResponseStatus.error(ExcelConstants.RESP_FAIL_STATUS, "导入任务处理失败", importCallback);
        } finally {
            // 删除temp文件
            if (null != taskMeta.getUploadOriginTempFile()) {
                taskMeta.getUploadOriginTempFile().delete();
            }
            if (null != importedMeta && null != importedMeta.getResultTempFile()) {
                importedMeta.getResultTempFile().delete();
            }
        }

        return ResponseStatus.ok(importCallback);
    }

    /**
     * 导出
     *
     * @return 返回经过文件路径
     */
    public static ResponseStatus<ExportCallback> exportExcel(ExportTaskMeta taskMeta) {
        ExportCallback exportCallback = new ExportCallback();
        exportCallback.setTaskNumber(taskMeta.getTaskNumber());
        // 更新导出任务状态
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        ExcelExportStrategy strategy = ExcelStrategySelector.getExportStrategy(taskMeta.getExportCode());
        UpdateExportHisResultParam updateExportHisResultParam = new UpdateExportHisResultParam();
        updateExportHisResultParam.setTaskNumber(taskMeta.getTaskNumber());
        updateExportHisResultParam.setExportStatus(ExcelExportStatusEnum.DOING);
        updateExportHisResultParam.setExportProgress(0);
        updateExportHisResultParam.setResultMsg(ExcelExportStatusEnum.DOING.getDescription());
        excelServerRequestService.updateExportHisResult(updateExportHisResultParam);
        // 导出数据到excel
        ExportedMeta exportedMeta = null;
        try {
            exportedMeta = strategy.doExport(taskMeta);
            log.info("[{}]导出结果{}", taskMeta.getTaskNumber(), JsonUtils.toJSONString(exportedMeta));
            FileUploadResponse uploadResponse = null;
            if (Objects.nonNull(exportedMeta)) {
                uploadResponse = ExcelUploadUtil.uploadExport(exportedMeta);
                log.info("[{}]上传结果{}", taskMeta.getTaskNumber(), JsonUtils.toJSONString(uploadResponse));
            }
            // 检查导出文件上传状态
            if (null == uploadResponse || ExcelConstants.RESP_SUCCESS_STATUS != uploadResponse.getCode()) {
                throw new BusinessException("上传导出结果文件异常");
            }
            exportCallback.setFilePath(uploadResponse.getUrl());
            exportCallback.setFileName(exportedMeta.getExportFileMeta().getExportFile().getName());
            exportCallback.setRowsSize(exportedMeta.getTotalRecord());
            // 导出成功更新导出结果
            excelServerRequestService.updateExportSuccessResult(taskMeta, exportedMeta, uploadResponse);
            log.info("[{}]导出任务完成", taskMeta.getTaskNumber());
        } catch (BusinessException e) {
            excelServerRequestService.updateExportErrorResult(taskMeta, e.getMessage());
            log.error("[{}]导出失败", taskMeta.getTaskNumber(), e);
            return ResponseStatus.error(500, "导出任务处理失败," + e.getMessage(), exportCallback);
        } catch (Exception e) {
            excelServerRequestService.updateExportErrorResult(taskMeta, null);
            log.error("[{}]导出失败", taskMeta.getTaskNumber(), e);
            return ResponseStatus.error(500, "导出任务处理失败", exportCallback);
        } finally {
            // 删除temp文件
            if (null != exportedMeta && null != exportedMeta.getExportFileMeta() && null != exportedMeta.getExportFileMeta().getExportFile()) {
                exportedMeta.getExportFileMeta().getExportFile().delete();
            }
        }
        return ResponseStatus.ok(exportCallback);
    }

}
