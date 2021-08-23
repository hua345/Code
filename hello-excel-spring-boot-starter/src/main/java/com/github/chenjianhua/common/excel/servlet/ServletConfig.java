package com.github.chenjianhua.common.excel.servlet;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Configuration
public class ServletConfig {

    @Resource
    private ImportServlet importServlet;

    @Resource
    private ExportServlet exportServlet;

    @Bean
    public ServletRegistrationBean importServletBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.addUrlMappings("/importExcel/async", "/importExcel/sync");
        registrationBean.setServlet(importServlet);
        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean exportServletBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.addUrlMappings("/exportExcel/async", "/exportExcel/sync");
        registrationBean.setServlet(exportServlet);
        return registrationBean;
    }
}
