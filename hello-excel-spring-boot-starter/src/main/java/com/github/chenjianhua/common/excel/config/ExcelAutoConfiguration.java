package com.github.chenjianhua.common.excel.config;

import com.github.chenjianhua.common.excel.service.ExcelServerRequestService;
import com.github.chenjianhua.common.excel.support.http.RestTemplateExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Slf4j
@Configuration
@ConditionalOnClass({ExcelAutoProperties.class})
@ConditionalOnWebApplication
@EnableConfigurationProperties({ExcelAutoProperties.class})
public class ExcelAutoConfiguration {
    @Autowired
    private ExcelAutoProperties excelAutoProperties;

    @Value("${spring.application.name}")
    private String serverName;

    @Bean(name = "restTemplateExcel")
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    @Qualifier(value = "restTemplateExcel")
    private RestTemplate restTemplate;

    @Bean
    public RestTemplateExcelService restTemplateService() {
        RestTemplateExcelService restTemplateExcelService = new RestTemplateExcelService(restTemplate);
        return restTemplateExcelService;
    }

    @Autowired
    private RestTemplateExcelService restTemplateExcelService;

    @Bean
    public ExcelServerRequestService excelServerRequestService() {
        log.info("excel服务bean初始化完成 导出服务信息:{}", excelAutoProperties.getExportServerName());
        ExcelServerRequestService excelServerRequestService = new ExcelServerRequestService(excelAutoProperties, serverName, restTemplateExcelService);
        return excelServerRequestService;
    }
}

