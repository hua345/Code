package com.github.chenjianhua.common.excel.controller;

import com.github.chenjianhua.common.excel.bo.ept.ExportTaskMeta;
import com.github.chenjianhua.common.excel.support.ExportTaskManager;
import com.github.chenjianhua.common.excel.vo.ExportCallback;
import com.github.chenjianhua.common.excel.vo.ExportExcelParam;
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
    public ResponseVO asyncExport(@RequestHeader(value = "auth_token", required = true) String authToken,
                                  @Valid @RequestBody ExportExcelParam param) {
        log.info("asyncExport收到导出请求:{}", JsonUtil.toJsonString(param));
        if (null == param || !StringUtils.hasText(param.getExportCode())) {
            return ResponseVO.fail("导出参数格式为{'exportCode':'导出类型','exportArg':'导出参数'}");
        }
        ExportTaskMeta exportTaskMeta = buildExportTaskMeta(param, authToken);
        exportTaskMeta.setSyncTask(false);
        ResponseVO<ExportCallback> exportResp = ExportTaskManager.excelExport(exportTaskMeta);
        log.info("asyncExport导出结果:{}", JsonUtil.toJsonString(exportResp));
        return ResponseVO.ok();
    }

    @ResponseBody
    @PostMapping("/sync")
    public ResponseVO<ExportCallback> syncExport(@RequestHeader(value = "auth_token", required = true) String authToken,
                                                 @Valid @RequestBody ExportExcelParam param) {
        log.info("syncExport收到导出请求:{}", JsonUtil.toJsonString(param));
        if (null == param || !StringUtils.hasText(param.getExportCode())) {
            return ResponseVO.fail("导出参数格式为{'exportCode':'导出类型','exportArg':'导出参数'}");
        }
        ExportTaskMeta exportTaskMeta = buildExportTaskMeta(param, authToken);
        exportTaskMeta.setSyncTask(true);
        ResponseVO<ExportCallback> exportResp = ExportTaskManager.excelExport(exportTaskMeta);
        log.info("syncExport导出结果:{}", JsonUtil.toJsonString(exportResp));
        return exportResp;
    }

    private ExportTaskMeta buildExportTaskMeta(ExportExcelParam param, String authToken) {
        ExportTaskMeta exportTaskMeta = new ExportTaskMeta();
        exportTaskMeta.setExportCode(param.getExportCode());
        exportTaskMeta.setExportArg(param.getExportArg());
        exportTaskMeta.setAuthToken(authToken);
        return exportTaskMeta;
    }
}
