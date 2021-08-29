package com.github.chenjianhua.common.excel.entity.exportexcel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@ToString
public class ExportTaskParam {
    /**
     * 导出型号（导出编号）
     */
    private String exportCode;
    /**
     * 导出任务编号
     */
    private String taskNumber;
    /**
     * 导出参数
     */
    private Object exportArg;
    /**
     * 是否同步任务
     */
    private boolean syncTask = true;
    /**
     * 登录token
     */
    private String authToken;
}
