package com.github.chenjianhua.common.excel.example;

import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskParam;

import java.io.IOException;

/**
 * @author chenjianhua
 * @date 2020/12/31
 */
public class ExportTest {

    public static void main(String[] args) throws IOException {
        TestCurrentStrategy testCurrentStrategy = new TestCurrentStrategy();
        ExportTaskParam meta = new ExportTaskParam();
        meta.setTaskNumber("KT".concat(System.currentTimeMillis() + ""));
        meta.setExportCode("KT-VM_TRADE_LOG");
        meta.setExportArg(null);
        meta.setSyncTask(true);
        testCurrentStrategy.doExport(meta);
    }
}
