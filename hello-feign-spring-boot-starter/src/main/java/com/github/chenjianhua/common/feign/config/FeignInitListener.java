package com.github.chenjianhua.common.feign.config;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.github.chenjianhua.common.feign.core.ThreadLocalHystrixConcurrencyStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * 服务启动时开始注册
 */
@Slf4j
@Configuration
public class FeignInitListener implements ApplicationListener<ApplicationStartingEvent> {
//public class FeignInitListener implements ApplicationContextInitializer {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {
        log.info("注册hystrix ThreadLocalHystrixConcurrencyStrategy");
        //List<HystrixCallableWrapper> wrappers = Arrays.asList(new RequestAttributeAwareCallableWrapper(), new MdcAwareCallableWrapper());
        //HystrixPlugins.getInstance().registerConcurrencyStrategy(new RequestContextHystrixConcurrencyStrategy(wrappers));
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new ThreadLocalHystrixConcurrencyStrategy());
    }
}
