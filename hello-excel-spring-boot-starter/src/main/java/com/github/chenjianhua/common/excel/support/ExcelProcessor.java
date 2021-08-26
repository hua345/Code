package com.github.chenjianhua.common.excel.support;

import com.github.chenjianhua.common.excel.bo.FileUploadResponse;
import com.github.chenjianhua.common.excel.bo.ept.ExportTaskMeta;
import com.github.chenjianhua.common.excel.bo.ept.ExportedMeta;
import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskParam;
import com.github.chenjianhua.common.excel.bo.ipt.ImportResultVo;
import com.github.chenjianhua.common.excel.enums.ExcelConstants;
import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import com.github.chenjianhua.common.excel.service.ExcelServerRequestService;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.chenjianhua.common.excel.util.ApplicationContextUtil;
import com.github.chenjianhua.common.excel.util.ExcelUploadUtil;
import com.github.chenjianhua.common.excel.vo.ExportCallback;
import com.github.chenjianhua.common.excel.vo.ImportCallback;
import com.github.chenjianhua.common.excel.vo.UpdateExportHisResultParam;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import com.github.common.resp.ResponseVO;
import com.github.chenjianhua.common.excel.support.ept.ExcelExportStrategy;
import com.github.chenjianhua.common.excel.vo.UpdateImportHisResultParam;
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
    public static ResponseVO<ImportCallback> importExcel(ImportTaskParam importTaskParam) {
        ImportCallback importCallback = new ImportCallback();
        importCallback.setTaskNumber(importTaskParam.getTaskNumber());
        ExcelImportStrategy strategy = ExcelStrategySelector.getImportStrategy(importTaskParam.getImportCode());
        // 更新导入任务状态
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        UpdateImportHisResultParam updateImportHisResultParam = new UpdateImportHisResultParam();
        updateImportHisResultParam.setTaskNumber(importTaskParam.getTaskNumber());
        updateImportHisResultParam.setImportStatus(ExcelExportStatusEnum.DOING);
        updateImportHisResultParam.setResultMsg(ExcelExportStatusEnum.DOING.getDescription());
        updateImportHisResultParam.setFileName(importTaskParam.getUploadOriginTempFile().getName());
        ImportResultVo importResultVo = null;
        try {
            importResultVo = strategy.doImport(importTaskParam);
            log.info("[{}]导入结果{}", importTaskParam.getTaskNumber(), JsonUtil.toJsonString(importResultVo));
            FileUploadResponse uploadResponse = null;
            if (Objects.nonNull(importResultVo)) {
                uploadResponse = ExcelUploadUtil.uploadImport(importResultVo.getResultTempFile());
                log.info("[{}]上传结果{}", importTaskParam.getTaskNumber(), JsonUtil.toJsonString(uploadResponse));
            }
            // 检查导出文件上传状态
            if (null == uploadResponse || ExcelConstants.RESP_SUCCESS_STATUS != uploadResponse.getCode()) {
                throw new BusinessException("上传导入结果文件异常");
            }
            importCallback.setFileName(updateImportHisResultParam.getFileName());
            importCallback.setTotalRecord(importResultVo.getTotalRecord());
            importCallback.setSuccessRecord(importResultVo.getSuccessRecord());
            importCallback.setFailedRecord(importResultVo.getFailedRecord());
            importCallback.setOriginOssFilePath(importTaskParam.getImportOssFilePath());
            importCallback.setResultOssFilePath(uploadResponse.getUrl());
            importCallback.setImportStatus(ExcelExportStatusEnum.SUCCESS);
            // 导出成功更新导出结果
            excelServerRequestService.updateImportSuccessResult(importTaskParam, importResultVo, uploadResponse);
            log.info("[{}]导入任务完成", importTaskParam.getTaskNumber());
        } catch (BusinessException e) {
            excelServerRequestService.updateImportErrorResult(importTaskParam, e.getMessage());
            log.error("[{}]导入失败", importTaskParam.getTaskNumber(), e);
            return ResponseVO.fail("导出任务处理失败," + e.getMessage(), importCallback);
        } catch (Exception e) {
            excelServerRequestService.updateImportErrorResult(importTaskParam, null);
            log.error("[{}]导入失败", importTaskParam.getTaskNumber(), e);
            return ResponseVO.fail("导入任务处理失败", importCallback);
        } finally {
            boolean uploadDelete = false, resultDelete = false;
            // 删除temp文件
            if (null != importTaskParam.getUploadOriginTempFile() &&
                    importTaskParam.getUploadOriginTempFile().exists()) {
                uploadDelete = importTaskParam.getUploadOriginTempFile().delete();
            }
            if (null != importResultVo && null != importResultVo.getResultTempFile()
                    && importResultVo.getResultTempFile().exists()) {
                resultDelete = importResultVo.getResultTempFile().delete();
            }
            log.info("删除temp文件uploadDelete:{},resultDelete:{}", uploadDelete, resultDelete);
        }

        return ResponseVO.ok(importCallback);
    }

    /**
     * 导出
     *
     * @return 返回经过文件路径
     */
    public static ResponseVO<ExportCallback> exportExcel(ExportTaskMeta taskMeta) {
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
            log.info("[{}]导出结果{}", taskMeta.getTaskNumber(), JsonUtil.toJsonString(exportedMeta));
            FileUploadResponse uploadResponse = null;
            if (Objects.nonNull(exportedMeta)) {
                uploadResponse = ExcelUploadUtil.uploadExport(exportedMeta);
                log.info("[{}]上传结果{}", taskMeta.getTaskNumber(), JsonUtil.toJsonString(uploadResponse));
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
            return ResponseVO.fail("导出任务处理失败," + e.getMessage(), exportCallback);
        } catch (Exception e) {
            excelServerRequestService.updateExportErrorResult(taskMeta, null);
            log.error("[{}]导出失败", taskMeta.getTaskNumber(), e);
            return ResponseVO.fail("导出任务处理失败", exportCallback);
        } finally {
            // 删除temp文件
            if (null != exportedMeta && null != exportedMeta.getExportFileMeta() && null != exportedMeta.getExportFileMeta().getExportFile()) {
                exportedMeta.getExportFileMeta().getExportFile().delete();
            }
        }
        return ResponseVO.ok(exportCallback);
    }

}
