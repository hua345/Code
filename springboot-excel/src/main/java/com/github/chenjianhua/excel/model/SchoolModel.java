package com.github.chenjianhua.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Data
public class SchoolModel {
    @ExcelProperty(value = "学校名称：")
    private String schoolName;
    @ExcelProperty(value = "专业名称：")
    private String universityMajor;
    @ExcelProperty(value = "姓名：")
    private String name;
}
