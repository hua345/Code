package com.github.chenjianhua.common.feign.core;

import java.util.concurrent.Callable;

/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
public interface HystrixCallableWrapper {
    /**
     * 包装Callable实例
     *
     * @param callable 待包装实例
     * @param <T>      返回类型
     * @return         包装后的实例
     */
    <T> Callable<T> wrap(Callable<T> callable);
}
