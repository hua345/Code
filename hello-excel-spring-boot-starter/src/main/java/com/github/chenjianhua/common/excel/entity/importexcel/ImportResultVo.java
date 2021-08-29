package com.github.chenjianhua.common.excel.entity.importexcel;

import com.github.chenjianhua.common.excel.enums.ExcelExportStatusEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenjianhua
 * @date 2021/3/25
 */
@Setter
@Getter
public class ImportResultVo {
    /**
     * 任务号
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
     * 成功记录数
     */
    private Long successRecord = 0L;
    /**
     * 失败记录数
     */
    private Long failedRecord = 0L;
    /**
     * 导入文件地址
     */
    private String originOssFilePath;
    /**
     * 导入结果文件地址
     */
    private String resultOssFilePath;
}
