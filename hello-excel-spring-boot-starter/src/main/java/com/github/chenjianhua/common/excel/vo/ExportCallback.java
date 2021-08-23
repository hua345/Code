package com.github.chenjianhua.common.excel.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author chenjianhua
 * @date 2020/12/23
 */
@Getter
@Setter
public class ExportCallback {
    private String taskNumber;
    private String fileName;
    private Long rowsSize;
    private String filePath;

    public ExportCallback() {
    }

    public ExportCallback(String fileName, Long rowsSize) {
        this.fileName = fileName;
        this.rowsSize = rowsSize;
    }
}
