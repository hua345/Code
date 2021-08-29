package com.github.chenjianhua.common.excel.support.ipt;

import com.github.chenjianhua.common.excel.entity.importexcel.ImportTaskParam;
import com.github.chenjianhua.common.excel.entity.importexcel.ImportTaskVo;

import java.io.IOException;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
public interface ExcelImportStrategy {

    /**
     * 执行导入
     *
     * @param meta 导入所需的原始数据
     * @return 导入结果
     * @throws IOException io异常
     */
    ImportTaskVo doImport(ImportTaskParam meta);

}
