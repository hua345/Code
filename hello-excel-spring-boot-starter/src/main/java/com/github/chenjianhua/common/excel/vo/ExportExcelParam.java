package com.github.chenjianhua.common.excel.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Data
public class ExportExcelParam {
    /**
     * 导出型号（导出编号）
     */
    @NotEmpty(message = "exportCode导出类型不能为空,参数格式为{'exportCode':'导出类型','exportArg':'导出参数'}")
    private String exportCode;
    /**
     * 导出参数
     */
    private Object exportArg;
}
