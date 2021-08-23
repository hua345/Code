package com.github.chenjianhua.common.feign.config;

import com.github.chenjianhua.common.feign.core.FeignResponseDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ConditionalOnClass 当类路径下有指定的类的条件下进行自动配置
 * EnableConfigurationProperties  类的属性和配置字段一一对应
 * @author haiping.huang
 * @date 2021/01/28
 */
@Slf4j
@Configuration
@ConditionalOnClass({FeignAutoProperties.class})
@ConditionalOnWebApplication
@EnableConfigurationProperties({ FeignAutoProperties.class})
public class HelloFeignAutoConfiguration {

    @Bean
    public FeignResponseDecoder feignResponseDecoder(ObjectFactory<HttpMessageConverters> messageConverters, FeignAutoProperties feignAutoProperties) {
        return new FeignResponseDecoder(messageConverters, feignAutoProperties);
    }
}
