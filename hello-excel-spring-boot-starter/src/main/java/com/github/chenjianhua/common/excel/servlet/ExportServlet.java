package com.github.chenjianhua.common.excel.servlet;

import com.github.chenjianhua.common.excel.bo.ept.ExportTaskMeta;
import com.github.chenjianhua.common.excel.enums.LoginInfoConstant;
import com.szkunton.common.ktcommon.vo.ResponseStatus;
import com.github.chenjianhua.common.excel.support.ExportTaskManager;
import com.github.chenjianhua.common.excel.util.ServletRespUtil;
import com.github.chenjianhua.common.excel.vo.ExportCallback;
import com.szkunton.common.ktjson.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Component
public class ExportServlet extends HttpServlet {
    @Resource
    private ServletAopAdapterService servletAopAdapterService;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        servletAopAdapterService.proxyExportRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseSb = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseSb.append(inputStr);
            }
            String exportParam = responseSb.toString();

            doExport(exportParam, request, response);
        } catch (Exception e) {
            log.error("导出解析参数异常", e);
        }
    }

    /**
     * 获取用户登录信息
     */
    private void handleToken(HttpServletRequest request, ExportTaskMeta exportTaskMeta) {
        String token = request.getHeader(LoginInfoConstant.AUTH_TOKEN_KEY);

        if (!StringUtils.isEmpty(token)) {
            exportTaskMeta.setAuthToken(token);
        }
    }

    private void doExport(String exportParam, HttpServletRequest request, HttpServletResponse response) {
        log.info("收到导出请求:{}", exportParam);

        ExportTaskMeta exportTaskMeta = JsonUtils.toBean(exportParam, ExportTaskMeta.class);
        if (null == exportTaskMeta || !StringUtils.hasText(exportTaskMeta.getExportCode())) {
            ResponseStatus responseStatus = ResponseStatus.error("exportCode导出类型不能为空,参数格式为{'exportCode':'导出类型','exportArg':'导出参数'}");
            ServletRespUtil.writeJson(responseStatus, response);
            return;
        }
        exportTaskMeta.setSyncTask(!request.getRequestURI().endsWith("async"));
        // 获取用户登录信息
        handleToken(request, exportTaskMeta);
        ResponseStatus<ExportCallback> exportResp = ExportTaskManager.excelExport(exportTaskMeta);
        ServletRespUtil.writeJson(exportResp, response);
    }
}
