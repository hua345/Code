package com.github.chenjianhua.common.excel.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenjianhua
 * @date 2021/4/1
 * 为aop拦截进行静态代理
 */
@Slf4j
@Component
public class ServletAopAdapterService {
    @Resource
    private ImportServlet importServlet;

    @Resource
    private ExportServlet exportServlet;

    public void proxyImportRequest(HttpServletRequest request, HttpServletResponse response) {
        importServlet.handleRequest(request, response);
    }

    public void proxyExportRequest(HttpServletRequest request, HttpServletResponse response) {
        exportServlet.handleRequest(request, response);
    }
}
