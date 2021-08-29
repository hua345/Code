package com.github.chenjianhua.common.excel.support;

import com.github.chenjianhua.common.excel.entity.FileUploadResponse;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskParam;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskVo;
import com.github.chenjianhua.common.excel.entity.importexcel.ImportTaskParam;
import com.github.chenjianhua.common.excel.entity.importexcel.ImportTaskVo;
import com.github.chenjianhua.common.excel.enums.ExcelConstants;
import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import com.github.chenjianhua.common.excel.service.ExcelServerRequestService;
import com.github.chenjianhua.common.excel.support.ipt.ExcelImportStrategy;
import com.github.chenjianhua.common.excel.util.ApplicationContextUtil;
import com.github.chenjianhua.common.excel.util.ExcelUploadUtil;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportResultVo;
import com.github.chenjianhua.common.excel.entity.importexcel.ImportResultVo;
import com.github.chenjianhua.common.excel.entity.log.UpdateExportHisResultParam;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import com.github.common.resp.ResponseVO;
import com.github.chenjianhua.common.excel.support.ept.ExcelExportStrategy;
import com.github.chenjianhua.common.excel.entity.log.UpdateImportHisResultParam;
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
    public static ResponseVO<ImportResultVo> importExcel(ImportTaskParam importTaskParam) {
        ImportResultVo importResultVo = new ImportResultVo();
        importResultVo.setTaskNumber(importTaskParam.getTaskNumber());
        ExcelImportStrategy strategy = ExcelStrategySelector.getImportStrategy(importTaskParam.getImportCode());
        // 更新导入任务状态
        ExcelServerRequestService excelServerRequestService = ApplicationContextUtil.getBean(ExcelServerRequestService.class);
        UpdateImportHisResultParam updateImportHisResultParam = new UpdateImportHisResultParam();
        updateImportHisResultParam.setTaskNumber(importTaskParam.getTaskNumber());
        updateImportHisResultParam.setImportStatus(ExcelExportStatusEnum.DOING);
        updateImportHisResultParam.setResultMsg(ExcelExportStatusEnum.DOING.getDescription());
        updateImportHisResultParam.setFileName(importTaskParam.getUploadOriginTempFile().getName());
        ImportTaskVo importTaskVo = null;
        try {
            importTaskVo = strategy.doImport(importTaskParam);
            log.info("[{}]导入结果{}", importTaskParam.getTaskNumber(), JsonUtil.toJsonString(importTaskVo));
            FileUploadResponse uploadResponse = null;
            if (Objects.nonNull(importTaskVo)) {
                uploadResponse = ExcelUploadUtil.uploadImport(importTaskVo.getResultTempFile());
                log.info("[{}]上传结果{}", importTaskParam.getTaskNumber(), JsonUtil.toJsonString(uploadResponse));
            }
            // 检查导出文件上传状态
            if (null == uploadResponse || ExcelConstants.RESP_SUCCESS_STATUS != uploadResponse.getCode()) {
                throw new BusinessException("上传导入结果文件异常");
            }
            importResultVo.setFileName(updateImportHisResultParam.getFileName());
            importResultVo.setTotalRecord(importTaskVo.getTotalRecord());
            importResultVo.setSuccessRecord(importTaskVo.getSuccessRecord());
            importResultVo.setFailedRecord(importTaskVo.getFailedRecord());
            importResultVo.setOriginOssFilePath(importTaskParam.getImportOssFilePath());
            importResultVo.setResultOssFilePath(uploadResponse.getUrl());
            importResultVo.setImportStatus(ExcelExportStatusEnum.SUCCESS);
            // 导出成功更新导出结果
            excelServerRequestService.updateImportSuccessResult(importTaskParam, importTaskVo, uploadResponse);
            log.info("[{}]导入任务完成", importTaskParam.getTaskNumber());
        } catch (BusinessException e) {
            excelServerRequestService.updateImportErrorResult(importTaskParam, e.getMessage());
            log.error("[{}]导入失败", importTaskParam.getTaskNumber(), e);
            return ResponseVO.fail("导出任务处理失败," + e.getMessage(), importResultVo);
        } catch (Exception e) {
            excelServerRequestService.updateImportErrorResult(importTaskParam, null);
            log.error("[{}]导入失败", importTaskParam.getTaskNumber(), e);
            return ResponseVO.fail("导入任务处理失败", importResultVo);
        } finally {
            boolean uploadDelete = false, resultDelete = false;
            // 删除temp文件
            if (null != importTaskParam.getUploadOriginTempFile() &&
                    importTaskParam.getUploadOriginTempFile().exists()) {
                uploadDelete = importTaskParam.getUploadOriginTempFile().delete();
            }
            if (null != importTaskVo && null != importTaskVo.getResultTempFile()
                    && importTaskVo.getResultTempFile().exists()) {
                resultDelete = importTaskVo.getResultTempFile().delete();
            }
            log.info("删除temp文件uploadDelete:{},resultDelete:{}", uploadDelete, resultDelete);
        }

        return ResponseVO.ok(importResultVo);
    }

    /**
     * 导出
     *
     * @return 返回经过文件路径
     */
    public static ResponseVO<ExportResultVo> exportExcel(ExportTaskParam taskMeta) {
        ExportResultVo exportResultVo = new ExportResultVo();
        exportResultVo.setTaskNumber(taskMeta.getTaskNumber());
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
        ExportTaskVo exportTaskVo = null;
        try {
            exportTaskVo = strategy.doExport(taskMeta);
            log.info("[{}]导出结果{}", taskMeta.getTaskNumber(), JsonUtil.toJsonString(exportTaskVo));
            FileUploadResponse uploadResponse = null;
            if (Objects.nonNull(exportTaskVo)) {
                uploadResponse = ExcelUploadUtil.uploadExport(exportTaskVo);
                log.info("[{}]上传结果{}", taskMeta.getTaskNumber(), JsonUtil.toJsonString(uploadResponse));
            }
            // 检查导出文件上传状态
            if (null == uploadResponse || ExcelConstants.RESP_SUCCESS_STATUS != uploadResponse.getCode()) {
                throw new BusinessException("上传导出结果文件异常");
            }
            exportResultVo.setFilePath(uploadResponse.getUrl());
            exportResultVo.setFileName(exportTaskVo.getExportFileBo().getExportFile().getName());
            exportResultVo.setRowsSize(exportTaskVo.getTotalRecord());
            // 导出成功更新导出结果
            excelServerRequestService.updateExportSuccessResult(taskMeta, exportTaskVo, uploadResponse);
            log.info("[{}]导出任务完成", taskMeta.getTaskNumber());
        } catch (BusinessException e) {
            excelServerRequestService.updateExportErrorResult(taskMeta, e.getMessage());
            log.error("[{}]导出失败", taskMeta.getTaskNumber(), e);
            return ResponseVO.fail("导出任务处理失败," + e.getMessage(), exportResultVo);
        } catch (Exception e) {
            excelServerRequestService.updateExportErrorResult(taskMeta, null);
            log.error("[{}]导出失败", taskMeta.getTaskNumber(), e);
            return ResponseVO.fail("导出任务处理失败", exportResultVo);
        } finally {
            // 删除temp文件
            if (null != exportTaskVo && null != exportTaskVo.getExportFileBo() && null != exportTaskVo.getExportFileBo().getExportFile()) {
                exportTaskVo.getExportFileBo().getExportFile().delete();
            }
        }
        return ResponseVO.ok(exportResultVo);
    }

}
