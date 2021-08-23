package com.github.chenjianhua.common.excel.bo.ept;

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
public class ExportFileMeta {
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
