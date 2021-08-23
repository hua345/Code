package com.github.chenjianhua.common.excel.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "hello.excel")
public class ExcelAutoProperties {
    /**
     * 导出服务地址
     */
    private String exportHttpIp;
    /**
     * 导出服务端口
     */
    private Integer exportPort = 8345;
    /**
     * 导出服务名称,默认使用导出服务名
     * 如果配置了服务器ip则使用ip,
     */
    private String exportServerName = "export-server";

    private Boolean closeExcelLog = false;
}
