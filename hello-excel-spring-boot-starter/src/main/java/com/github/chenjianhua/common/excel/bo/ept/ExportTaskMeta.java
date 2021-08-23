package com.github.chenjianhua.common.excel.bo.ept;

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
public class ExportTaskMeta {
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
     * 当前导出数据参数（用到的时候需要强制类型转换），第一次导出为空
     */
    private Object currentExportAttr;
    /**
     * 是否同步任务
     */
    private boolean syncTask = true;
    /**
     * 登录token
     */
    private String authToken;
}
