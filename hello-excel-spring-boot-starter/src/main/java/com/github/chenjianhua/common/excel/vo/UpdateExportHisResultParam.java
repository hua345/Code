package com.github.chenjianhua.common.excel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import lombok.Data;


import java.time.LocalDateTime;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Data
public class UpdateExportHisResultParam {
    /**
     * 导出任务号
     */
    private String taskNumber;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件OSS存储路径
     */
    private String filePath;
    /**
     * 导出总记录数
     */
    private Long totalRecord;
    /**
     * 导出状态
     */
    private ExcelExportStatusEnum exportStatus = ExcelExportStatusEnum.DEFAULT;
    /**
     * 导出进度
     */
    private Integer exportProgress;
    /**
     * 结果描述
     */
    private String resultMsg;
    /**
     * 导出结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
