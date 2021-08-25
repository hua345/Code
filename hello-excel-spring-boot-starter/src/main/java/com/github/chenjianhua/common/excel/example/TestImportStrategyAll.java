package com.github.chenjianhua.common.excel.example;

import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskMeta;
import com.github.chenjianhua.common.excel.annotation.ImportStrategy;
import com.github.chenjianhua.common.excel.support.ipt.AbstractExcelImportAll;
import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/3/25
 */
@Slf4j
@Component
@ImportStrategy(strategyCode = "test_import")
public class TestImportStrategyAll extends AbstractExcelImportAll<TestModel> {
    /**
     * @param rowData 解析后的单行数据
     */
    @Override
    public void rowDataCheck(TestModel rowData) {
        log.info(JsonUtil.toJsonString(rowData));
    }

    /**
     * @param rowData 检查成功后返回的所有数据
     */
    @Override
    public void importedAllRowHandle(List<TestModel> rowData) {
        log.info("读取的数据行数:{}", rowData.size());
    }

    public static void main(String[] args) throws IOException {
        String fileName = "C:\\Users\\chenjh91\\Desktop" + File.separator + "test.xlsx";
        File file = new File(fileName);
        TestImportStrategyAll testImportStrategy = new TestImportStrategyAll();
        ImportTaskMeta importTaskMeta = new ImportTaskMeta();
        importTaskMeta.setUploadOriginTempFile(file);
        testImportStrategy.doImport(importTaskMeta);
    }
}
