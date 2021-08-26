package com.github.chenjianhua.excel.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @author chenjianhua
 * @date 2020/12/23
 */
@Getter
@Setter
@HeadRowHeight(value = 35) // 表头行高
@ContentRowHeight(value = 25) // 内容行高
public class TestExportModel {

    @ExcelProperty(value = "Id")
    @ExcelIgnore
    private Integer id;

    @ExcelProperty(value = {"图书", "书名"})
    private String bookName;

    @ExcelProperty(value = {"图书", "价格"})
    @ColumnWidth(20)
    private String bookPrice;

    @ExcelProperty(value = {"图书", "描述"})
    private String desc;


}
