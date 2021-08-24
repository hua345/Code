package com.github.chenjianhua.hellospringbootstarter.config;

import com.github.chenjianhua.hellospringbootstarter.controller.HelloController;
import com.github.chenjianhua.hellospringbootstarter.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({HelloService.class})
@ConditionalOnWebApplication
@EnableConfigurationProperties({HelloProperties.class})
public class HelloAutoConfiguration {
    @Autowired
    private HelloProperties helloProperties;

    @Bean
    @ConditionalOnMissingBean(HelloService.class)
    public HelloService helloService() {
        HelloService helloService = new HelloService(helloProperties);
        return helloService;
    }

    @Bean
    public HelloController helloController(HelloService helloService) {
        return new HelloController(helloService);
    }

}

