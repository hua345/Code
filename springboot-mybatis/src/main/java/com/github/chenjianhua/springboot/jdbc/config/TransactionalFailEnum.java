package com.github.chenjianhua.springboot.jdbc.config;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author chenjianhua
 * @date 2021/8/23
 */
@Getter
@AllArgsConstructor
public enum TransactionalFailEnum {
    protectClassMethod(1, "非public方法导致@Transactional失效"),
    sameClassMethod(2,"同一个类中方法调用，导致@Transactional失效"),
    rollbackForError(3,"@Transactional注解属性rollbackFor设置错误"),
    propagationError(4,"@Transactional 注解属性propagation设置错误"),
    tryCatchException(5, "异常被catch捕获导致@Transactional失效");

    /**
     * @JsonValue: 在序列化时，只序列化 @JsonValue 注解标注的值,swagger也返回@JsonValue的内容
     * @JsonCreator: 在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
     */
    @JsonValue
    Integer type;
    String description;
}
