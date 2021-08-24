package com.github.chenjianhua.common.excel.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author chenjianhua
 * @date 2020/12/23
 */
@Getter
@AllArgsConstructor
public enum ExcelExportStatusEnum implements BaseEnum<Integer> {

    /**
     * 待处理
     */
    DEFAULT(0, "待处理"),
    /**
     * 处理中
     */
    DOING(1, "处理中"),
    /**
     * 处理成功
     */
    SUCCESS(2, "处理成功"),
    /**
     * 处理失败
     */
    FAIL(3, "处理失败");

    Integer type;
    String description;

    /**
     * @JsonValue: 在序列化时，只序列化 @JsonValue 注解标注的值,swagger也返回@JsonValue的内容
     * @JsonCreator: 在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
     */
    @JsonValue
    @Override
    public Integer getType() {
        return type;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ExcelExportStatusEnum findByType(Integer type) {
        for (ExcelExportStatusEnum item : ExcelExportStatusEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}
