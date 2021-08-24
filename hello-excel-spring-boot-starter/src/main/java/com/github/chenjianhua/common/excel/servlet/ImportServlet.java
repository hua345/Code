package com.github.chenjianhua.common.excel.servlet;

import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskMeta;
import com.github.chenjianhua.common.excel.enums.LoginInfoConstant;
import com.github.chenjianhua.common.excel.util.ServletRespUtil;
import com.github.chenjianhua.common.excel.support.ImportTaskManager;
import com.github.chenjianhua.common.excel.vo.ImportCallback;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import com.github.common.resp.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Component
public class ImportServlet extends HttpServlet {
    private static final String IMPORT_TYPE = "importType";

    private static final String UPLOAD_FILE = "file";

    @Resource
    private ServletAopAdapterService servletAopAdapterService;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        servletAopAdapterService.proxyImportRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
            Map<String, String[]> paramMap = multipartRequest.getParameterMap();
            String importType = multipartRequest.getParameter(IMPORT_TYPE);
            MultipartFile file = multipartRequest.getFile(UPLOAD_FILE);
            String fileName = file.getOriginalFilename();
            if (!StringUtils.hasLength(fileName) || 0 == file.getSize()) {
                throw new BusinessException("上传文件为空");
            }
            String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!"xlsx".equals(fileSuffix) && !"xls".equals(fileSuffix)) {
                throw new BusinessException("文件格式错误");
            }
            doImport(file, importType, paramMap, request, response);
        } catch (Exception e) {
            log.info("导入请求解析异常:{}", JsonUtil.toJsonString(e));
        }
    }

    private void doImport(MultipartFile file, String importType, Map<String, String[]> paramMap, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasText(importType)) {
            ResponseVO responseStatus = ResponseVO.fail("importType导入类型不能为空");
            ServletRespUtil.writeJson(responseStatus, response);
            return;
        }
        ImportTaskMeta importTaskMeta = new ImportTaskMeta();
        importTaskMeta.setSyncTask(!request.getRequestURI().endsWith("async"));
        importTaskMeta.setFile(file);
        importTaskMeta.setImportCode(importType);
        // 导入参数
        Map<String, Object> importArgs = new HashMap<>(8);
        paramMap.entrySet().forEach(item -> {
            if (null != item.getValue() && !IMPORT_TYPE.equals(item.getKey()) && item.getValue().length >= 1) {
                importArgs.put(item.getKey(), item.getValue()[0]);
            }
        });
        importTaskMeta.setImportArg(importArgs);
        // 获取用户登录信息
        handleToken(request, importTaskMeta);
        ResponseVO<ImportCallback> importResp = ImportTaskManager.excelImport(importTaskMeta);
        ServletRespUtil.writeJson(importResp, response);
    }

    /**
     * 获取用户登录信息
     */
    private void handleToken(HttpServletRequest request, ImportTaskMeta importTaskMeta) {
        String token = request.getHeader(LoginInfoConstant.AUTH_TOKEN_KEY);
        if (!StringUtils.isEmpty(token)) {
            importTaskMeta.setAuthToken(token);
        }
    }
}
