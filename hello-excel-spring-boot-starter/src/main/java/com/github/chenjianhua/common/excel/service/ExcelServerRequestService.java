package com.github.chenjianhua.common.excel.service;

import com.github.chenjianhua.common.excel.bo.ept.ExportTaskMeta;
import com.github.chenjianhua.common.excel.bo.ept.ExportedMeta;
import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskParam;
import com.github.chenjianhua.common.excel.bo.ipt.ImportResultVo;
import com.github.chenjianhua.common.excel.config.ExcelAutoProperties;
import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import com.github.chenjianhua.common.excel.vo.AddImportHisParam;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import com.github.common.resp.ResponseVO;
import com.github.chenjianhua.common.excel.bo.FileUploadResponse;
import com.github.chenjianhua.common.excel.support.http.RestTemplateExcelService;
import com.github.chenjianhua.common.excel.util.IpUtil;
import com.github.chenjianhua.common.excel.vo.AddExportHisParam;
import com.github.chenjianhua.common.excel.vo.UpdateExportHisResultParam;
import com.github.chenjianhua.common.excel.vo.UpdateImportHisResultParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Component
public class ExcelServerRequestService {

    private ExcelAutoProperties excelAutoProperties;

    private String serverName;

    private RestTemplateExcelService restTemplateExcelService;

    private ExcelServerRequestService() {

    }

    public ExcelServerRequestService(ExcelAutoProperties excelAutoProperties, String serverName, RestTemplateExcelService restTemplateExcelService) {
        this.excelAutoProperties = excelAutoProperties;
        this.serverName = serverName;
        this.restTemplateExcelService = restTemplateExcelService;
    }

    public String getCurrentServerName() {
        return this.serverName;
    }

    public ResponseVO postJson(String uriPath, String json, String authToken) {
        if (excelAutoProperties.getCloseExcelLog()) {
            log.info("忽略导入导出记录");
            return ResponseVO.ok();
        }
        StringBuilder sb = new StringBuilder();
        ResponseEntity<String> resp;
        if (StringUtils.hasText(excelAutoProperties.getExportHttpIp())) {
            sb.append(excelAutoProperties.getExportHttpIp()).append(":")
                    .append(excelAutoProperties.getExportPort()).append("/").append(uriPath);
            resp = RestTemplateExcelService.postJson(sb.toString(), json, authToken);
        } else {
            resp = restTemplateExcelService.postJsonByServerName(excelAutoProperties.getExportServerName(), uriPath, json, authToken);
        }
        if (null != resp.getStatusCode() && resp.getStatusCode().is2xxSuccessful()) {
            return JsonUtil.toBean(resp.getBody(), ResponseVO.class);
        }
        log.error("导出服务异常:{}", JsonUtil.toJsonString(resp));
        throw new BusinessException("导出服务异常，请稍后再试");
    }

    /**
     * 创建导出任务
     */
    public ResponseVO addExportHis(ExportTaskMeta taskMeta) {
        // 创建任务
        AddExportHisParam addExportHisParam = new AddExportHisParam();
        addExportHisParam.setExportType(taskMeta.getExportCode());
        addExportHisParam.setTaskNumber(taskMeta.getTaskNumber());
        addExportHisParam.setExportParam(JsonUtil.toJsonString(taskMeta.getExportArg()));
        addExportHisParam.setSyncTask(taskMeta.isSyncTask());
        addExportHisParam.setExportOrigin(this.getCurrentServerName());
        addExportHisParam.setExportOriginIp(IpUtil.getServerIp());
        addExportHisParam.setStartTime(LocalDateTime.now());
        log.info("创建导出任务 param:{}", JsonUtil.toJsonString(addExportHisParam));
        return this.postJson("addExportHis", JsonUtil.toJsonString(addExportHisParam), taskMeta.getAuthToken());
    }

    public ResponseVO updateExportErrorResult(ExportTaskMeta taskMeta, String errorMsg) {
        UpdateExportHisResultParam updateExportHisResultParam = new UpdateExportHisResultParam();
        updateExportHisResultParam.setTaskNumber(taskMeta.getTaskNumber());
        updateExportHisResultParam.setExportStatus(ExcelExportStatusEnum.FAIL);
        updateExportHisResultParam.setExportProgress(100);
        if (StringUtils.hasText(errorMsg)) {
            updateExportHisResultParam.setResultMsg(errorMsg);
        } else {
            updateExportHisResultParam.setResultMsg(ExcelExportStatusEnum.FAIL.getDescription());
        }
        updateExportHisResultParam.setEndTime(LocalDateTime.now());
        return this.updateExportHisResult(updateExportHisResultParam);
    }

    public ResponseVO updateExportSuccessResult(ExportTaskMeta taskMeta, ExportedMeta exportedMeta, FileUploadResponse uploadResponse) {
        UpdateExportHisResultParam updateExportHisResultParam = new UpdateExportHisResultParam();
        updateExportHisResultParam.setTaskNumber(taskMeta.getTaskNumber());
        updateExportHisResultParam.setExportStatus(ExcelExportStatusEnum.SUCCESS);
        updateExportHisResultParam.setExportProgress(100);
        updateExportHisResultParam.setResultMsg(ExcelExportStatusEnum.SUCCESS.getDescription());
        updateExportHisResultParam.setTotalRecord(exportedMeta.getTotalRecord());
        updateExportHisResultParam.setEndTime(LocalDateTime.now());
        updateExportHisResultParam.setFilePath(uploadResponse.getUrl());
        updateExportHisResultParam.setFileName(exportedMeta.getExportFileMeta().getExportFile().getName());
        return this.updateExportHisResult(updateExportHisResultParam);
    }

    /**
     * 更新导出任务
     */
    public ResponseVO updateExportHisResult(UpdateExportHisResultParam param) {
        log.info("更新导出任务 param:{}", JsonUtil.toJsonString(param));
        return this.postJson("updateExportHisResult", JsonUtil.toJsonString(param), null);
    }

    /**
     * 创建导入任务
     */
    public ResponseVO addImportHis(ImportTaskParam taskMeta) {
        // 创建任务
        AddImportHisParam addImportHisParam = new AddImportHisParam();
        addImportHisParam.setImportType(taskMeta.getImportCode());
        addImportHisParam.setTaskNumber(taskMeta.getTaskNumber());
        addImportHisParam.setImportParam(JsonUtil.toJsonString(taskMeta.getImportArg()));
        addImportHisParam.setSyncTask(taskMeta.isSyncTask());
        addImportHisParam.setImportOrigin(this.getCurrentServerName());
        addImportHisParam.setImportOriginIp(IpUtil.getServerIp());
        addImportHisParam.setStartTime(LocalDateTime.now());
        log.info("创建导入任务 param:{}", JsonUtil.toJsonString(addImportHisParam));
        return this.postJson("addImportHis", JsonUtil.toJsonString(addImportHisParam), taskMeta.getAuthToken());
    }

    public ResponseVO updateImportErrorResult(ImportTaskParam taskMeta, String errorMsg) {
        UpdateImportHisResultParam updateImportHisResultParam = new UpdateImportHisResultParam();
        updateImportHisResultParam.setTaskNumber(taskMeta.getTaskNumber());
        updateImportHisResultParam.setFileName(taskMeta.getUploadOriginTempFile().getName());
        updateImportHisResultParam.setImportStatus(ExcelExportStatusEnum.FAIL);
        updateImportHisResultParam.setImportFilePath(taskMeta.getImportOssFilePath());
        if (StringUtils.hasText(errorMsg)) {
            updateImportHisResultParam.setResultMsg(errorMsg);
        } else {
            updateImportHisResultParam.setResultMsg(ExcelExportStatusEnum.FAIL.getDescription());
        }
        updateImportHisResultParam.setEndTime(LocalDateTime.now());
        return this.updateImportHisResult(updateImportHisResultParam);
    }

    public ResponseVO updateImportSuccessResult(ImportTaskParam taskMeta, ImportResultVo importResultVo, FileUploadResponse uploadResponse) {
        UpdateImportHisResultParam updateImportHisResultParam = new UpdateImportHisResultParam();
        updateImportHisResultParam.setTaskNumber(taskMeta.getTaskNumber());
        updateImportHisResultParam.setFileName(taskMeta.getUploadOriginTempFile().getName());
        updateImportHisResultParam.setImportStatus(ExcelExportStatusEnum.SUCCESS);
        updateImportHisResultParam.setResultMsg(ExcelExportStatusEnum.SUCCESS.getDescription());
        updateImportHisResultParam.setTotalRecord(importResultVo.getTotalRecord());
        updateImportHisResultParam.setSuccessRecord(importResultVo.getSuccessRecord());
        updateImportHisResultParam.setFailedRecord(importResultVo.getFailedRecord());
        updateImportHisResultParam.setEndTime(LocalDateTime.now());
        updateImportHisResultParam.setImportFilePath(taskMeta.getImportOssFilePath());
        updateImportHisResultParam.setResultFilePath(uploadResponse.getUrl());
        return this.updateImportHisResult(updateImportHisResultParam);
    }

    /**
     * 更新导入任务
     */
    public ResponseVO updateImportHisResult(UpdateImportHisResultParam param) {
        log.info("更新导入任务 param:{}", JsonUtil.toJsonString(param));
        return this.postJson("updateImportHisResult", JsonUtil.toJsonString(param), null);
    }

}