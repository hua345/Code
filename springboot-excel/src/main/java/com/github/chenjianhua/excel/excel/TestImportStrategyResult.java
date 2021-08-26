package com.github.chenjianhua.excel.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.github.chenjianhua.common.excel.annotation.ImportStrategy;
import com.github.chenjianhua.common.excel.bo.ipt.ImportDataBo;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractModelAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractModelResultAnalysisEventListener;
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
@ImportStrategy(strategyCode = "test_importResult")
public class TestImportStrategyResult extends AbstractImportTemplate<TestModel> {
    public TestImportStrategyResult() {
        this.setSheetName("图书");
        this.setHeadRowNumber(2);
    }

    @Override
    public AbstractModelAnalysisEventListener<TestModel> defaultAnalysisListener(ImportDataBo importDataBo) {
        SchoolModel schoolModel = (SchoolModel) importDataBo.getImportTaskParam().getImportArg().get("schoolModel");
        log.info("schoolModel:{}", JsonUtil.toJsonString(schoolModel));
        return new AbstractModelResultAnalysisEventListener<TestModel>(false, 4) {
            @Override
            public void processData(TestModel rowData, AnalysisContext context) {
                log.info(JsonUtil.toJsonString(rowData));
            }
        };
    }
}
