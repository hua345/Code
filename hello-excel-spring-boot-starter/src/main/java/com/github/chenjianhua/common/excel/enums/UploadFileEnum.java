package com.github.chenjianhua.common.excel.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Getter
@AllArgsConstructor
public enum UploadFileEnum {

    LOCAL_FILE("local", "本地文件"),

    OSS("oss", "阿里Oss");

    /**
     * @JsonValue: 在序列化时，只序列化 @JsonValue 注解标注的值,swagger也返回@JsonValue的内容
     * @JsonCreator: 在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
     */
    @JsonValue
    String type;
    String description;
}
