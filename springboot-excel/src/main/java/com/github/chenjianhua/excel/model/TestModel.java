package com.github.chenjianhua.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

/**
 * @author chenjianhua
 * @date 2020/12/23
 */
@Getter
@Setter
public class TestModel {
    /**
     * 书名
     */
    @NotEmpty(message = "书名不能为空")
    @ExcelProperty(value = "书名")
    private String bookName;
    /**
     * 价格
     */
    @ExcelProperty(value = "价格")
    private String bookPrice;
}
