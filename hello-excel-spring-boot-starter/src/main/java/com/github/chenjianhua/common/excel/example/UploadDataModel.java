package com.github.chenjianhua.common.excel.example;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenjianhua
 * @date 2021/3/25
 */
@Getter
@Setter
public class UploadDataModel {
    @ExcelProperty(value = "客户名称")
    private String customerName;

    @ExcelProperty(value = "工厂编号")
    private String nodeCode;

    @ExcelProperty(value = "商品名称")
    private String productName;
}
