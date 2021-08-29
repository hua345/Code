package com.github.chenjianhua.common.excel.controller;

import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskParam;
import com.github.chenjianhua.common.excel.support.ExportTaskManager;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportResultVo;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportExcelParam;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.resp.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Slf4j
@Controller
@RequestMapping("/exportExcel")
public class ExportExcelController {
    @ResponseBody
    @PostMapping("/async")
    public ResponseVO<ExportResultVo> asyncExport(@RequestHeader(value = "auth_token", required = true) String authToken,
                                                  @Valid @RequestBody ExportExcelParam param) {
        log.info("asyncExport收到导出请求:{}", JsonUtil.toJsonString(param));
        if (null == param || !StringUtils.hasText(param.getExportCode())) {
            return ResponseVO.fail("导出参数格式为{'exportCode':'导出类型','exportArg':'导出参数'}");
        }
        ExportTaskParam exportTaskParam = buildExportTaskMeta(param, authToken);
        exportTaskParam.setSyncTask(false);
        ResponseVO<ExportResultVo> exportResp = ExportTaskManager.excelExport(exportTaskParam);
        log.info("asyncExport导出结果:{}", JsonUtil.toJsonString(exportResp));
        return ResponseVO.ok();
    }

    @ResponseBody
    @PostMapping("/sync")
    public ResponseVO<ExportResultVo> syncExport(@RequestHeader(value = "auth_token", required = true) String authToken,
                                                 @Valid @RequestBody ExportExcelParam param) {
        log.info("syncExport收到导出请求:{}", JsonUtil.toJsonString(param));
        if (null == param || !StringUtils.hasText(param.getExportCode())) {
            return ResponseVO.fail("导出参数格式为{'exportCode':'导出类型','exportArg':'导出参数'}");
        }
        ExportTaskParam exportTaskParam = buildExportTaskMeta(param, authToken);
        exportTaskParam.setSyncTask(true);
        ResponseVO<ExportResultVo> exportResp = ExportTaskManager.excelExport(exportTaskParam);
        log.info("syncExport导出结果:{}", JsonUtil.toJsonString(exportResp));
        return exportResp;
    }

    private ExportTaskParam buildExportTaskMeta(ExportExcelParam param, String authToken) {
        ExportTaskParam exportTaskParam = new ExportTaskParam();
        exportTaskParam.setExportCode(param.getExportCode());
        exportTaskParam.setExportArg(param.getExportArg());
        exportTaskParam.setAuthToken(authToken);
        return exportTaskParam;
    }
}
