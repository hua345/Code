package com.github.chenjianhua.common.excel.example;

import com.alibaba.excel.context.AnalysisContext;
import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskMeta;
import com.github.chenjianhua.common.excel.annotation.ImportStrategy;
import com.github.chenjianhua.common.excel.support.ipt.AbstractExcelImport;
import com.szkunton.common.ktjson.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author chenjianhua
 * @date 2021/3/25
 */
@Slf4j
@Component
@ImportStrategy(strategyCode = "test_import")
public class TestImportStrategy extends AbstractExcelImport<UploadDataModel> {
    /**
     * @param rowData 解析后的单行数据
     */
    @Override
    public void importedRowHandle(UploadDataModel rowData) {
        log.info(JsonUtils.toJSONString(rowData));
    }

    public static void main(String[] args) throws IOException {
        String fileName = "/Users/chenjianhua/Desktop" + File.separator + "商品交易明细报表1275169920506234600.xlsx";
        File file = new File(fileName);
        TestImportStrategy testImportStrategy = new TestImportStrategy();
        ImportTaskMeta importTaskMeta = new ImportTaskMeta();
        importTaskMeta.setUploadOriginTempFile(file);
        testImportStrategy.doImport(importTaskMeta);
    }

}
