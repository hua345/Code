package com.github.chenjianhua.common.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExportStrategy {
    /**
     * 策略名称
     */
    String name() default  "";
    /**
     * 策略编号
     */
    String strategyCode();
}
