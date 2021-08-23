package com.github.chenjianhua.common.excel.bo.ept;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@ToString
public class ExportedMeta {
    /**
     * 导出任务编号
     */
    private String taskNumber;
    /**
     * 导出参数
     */
    private Object exportArg;
    /**
     * 是否同步
     */
    private boolean syncTask = true;
    /**
     * 开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    /**
     * 文件大小 KB
     */
    private Long fileSize;
    /**
     * 总记录数
     */
    private Long totalRecord;
    /**
     * 已导出总记录数
     */
    private ExportFileMeta exportFileMeta;
}
