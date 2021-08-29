package com.github.chenjianhua.common.excel.support.ept;

import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskParam;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskVo;

import java.io.IOException;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
public interface ExcelExportStrategy {

    /**
     * 执行导出
     *
     * @param meta 导出所需的原始数据
     * @return 导出结果
     * @throws IOException io异常
     */
    ExportTaskVo doExport(ExportTaskParam meta) throws IOException;
}
