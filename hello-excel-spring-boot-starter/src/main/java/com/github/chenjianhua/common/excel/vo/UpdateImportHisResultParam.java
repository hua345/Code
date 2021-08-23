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
public class UpdateImportHisResultParam {
    /**
     * 导入任务编号
     */
    private String taskNumber;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 导入总记录数
     */
    private Long totalRecord;
    /**
     * 导入状态
     */
    private ExcelExportStatusEnum importStatus = ExcelExportStatusEnum.DEFAULT;
    /**
     * 结果描述
     */
    private String resultMsg;
    /**
     * 导入结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    /**
     * 导入文件地址
     */
    private String importFilePath;
    /**
     * 导入结果文件地址
     */
    private String resultFilePath;
    /**
     * 成功记录数
     */
    private Long successRecord;
    /**
     * 失败记录数
     */
    private Long failedRecord;
}
