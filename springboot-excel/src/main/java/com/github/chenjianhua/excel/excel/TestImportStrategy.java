package com.github.chenjianhua.excel.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.github.chenjianhua.common.excel.annotation.ImportStrategy;
import com.github.chenjianhua.common.excel.entity.importexcel.ImportDataBo;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractModelAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractModelWriteFileAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.template.AbstractImportTemplate;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.excel.model.SchoolModel;
import com.github.chenjianhua.excel.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author chenjianhua
 * @date 2021/3/25
 */
@Slf4j
@Component
@ImportStrategy(strategyCode = "test_import")
public class TestImportStrategy extends AbstractImportTemplate<TestModel> {
    public TestImportStrategy() {
        this.setSheetName("图书");
        this.setHeadRowNumber(2);
    }


    @Override
    public AbstractModelAnalysisEventListener<TestModel> defaultAnalysisListener(ImportDataBo importDataBo) {
        SchoolModel schoolModel = (SchoolModel) importDataBo.getImportTaskParam().getImportArg().get("schoolModel");
        log.info("schoolModel:{}", JsonUtil.toJsonString(schoolModel));
        return new AbstractModelWriteFileAnalysisEventListener<TestModel>(false,3) {
            @Override
            public void processData(TestModel rowData, AnalysisContext context) {
                log.info(JsonUtil.toJsonString(rowData));
            }
        };
    }
}
