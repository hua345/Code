package com.github.chenjianhua.common.excel.bo.ipt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@ToString
public class ImportResultVo {
    /**
     * 导出任务编号
     */
    private String taskNumber;
    /**
     * 导入参数
     */
    private Object importArg;
    /**
     * 导出结果文件
     */
    protected File resultTempFile;
    /**
     * 总记录数
     */
    private Long totalRecord = 0L;
    /**
     * 导入成功条数
     */
    private Long successRecord = 0L;
    /**
     * 导入失败条数
     */
    private Long failedRecord = 0L;
    /**
     * 是否同步
     */
    private boolean syncTask = true;
}
