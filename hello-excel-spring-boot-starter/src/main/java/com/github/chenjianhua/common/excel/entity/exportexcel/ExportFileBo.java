package com.github.chenjianhua.common.excel.entity.exportexcel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@ToString
public class ExportFileBo {
    /**
     * 导出文件
     */
    private File exportFile;
    /**
     * 文件大小 KB
     */
    private Long fileSize;
    /**
     * 总记录数
     */
    private Long totalRecord;
    /**
     * 导出次数
     */
    private Integer exportTimes;
}
