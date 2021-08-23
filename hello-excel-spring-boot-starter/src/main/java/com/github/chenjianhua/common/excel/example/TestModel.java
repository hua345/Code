package com.github.chenjianhua.common.excel.example;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

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
    @ExcelProperty(value = "书名", index = 0)
    private String bookName;
    /**
     * 价格
     */
    @ExcelProperty(value = "价格", index = 1)
    private BigDecimal bookPrice;
}
