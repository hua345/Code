package com.github.chenjianhua.common.feign.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "hello.feign")
public class FeignAutoProperties {
    /**
     * 是否包装解码
     */
    private Boolean packDecode = true;

}
