package com.github.chenjianhua.common.feign.config;

import com.github.chenjianhua.common.feign.core.HystrixCallableWrapper;
import com.github.chenjianhua.common.feign.core.MdcAwareCallableWrapper;
import com.github.chenjianhua.common.feign.core.RequestAttributeAwareCallableWrapper;
import com.github.chenjianhua.common.feign.core.RequestContextHystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.szkunton.common.ktfeign.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
@Slf4j
public class HystrixConcurrencyConfiguration {

    public static void registerConcurrencyStrategy() {
        List<HystrixCallableWrapper> wrappers = Arrays.asList(new RequestAttributeAwareCallableWrapper(), new MdcAwareCallableWrapper());
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new RequestContextHystrixConcurrencyStrategy(wrappers));
        //HystrixPlugins.getInstance().registerConcurrencyStrategy(new ThreadLocalHystrixConcurrencyStrategy());
    }
}
