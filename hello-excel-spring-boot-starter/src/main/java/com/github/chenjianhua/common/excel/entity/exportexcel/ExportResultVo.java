package com.github.chenjianhua.common.excel.entity.exportexcel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author chenjianhua
 * @date 2020/12/23
 */
@Getter
@Setter
public class ExportResultVo {
    /**
     * 导出任务编号
     */
    private String taskNumber;

    /**
     * 导出文件名
     */
    private String fileName;
    /**
     * 导出文件行数
     */
    private Long rowsSize;
    /**
     * 文件路径
     */
    private String filePath;
}
