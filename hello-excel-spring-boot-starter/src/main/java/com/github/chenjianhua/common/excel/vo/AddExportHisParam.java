package com.github.chenjianhua.common.excel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Data
public class AddExportHisParam {
    /**
     * 导出类型
     */
    private String exportType;

    /**
     * 导出任务编号
     */
    private String taskNumber;
    /**
     * 导出来源
     */
    private String exportOrigin;
    /**
     * 导出服务IP
     */
    private String exportOriginIp;

    /**
     * 导出参数
     */
    private String exportParam;
    /**
     * 是否为同步任务(0:异步任务,1:同步任务)
     */
    private boolean syncTask;
    /**
     * 导出开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
}
