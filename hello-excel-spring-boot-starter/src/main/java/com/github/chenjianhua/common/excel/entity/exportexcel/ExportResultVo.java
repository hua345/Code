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

    private String fileName;
    private Long rowsSize;
    private String filePath;
}
