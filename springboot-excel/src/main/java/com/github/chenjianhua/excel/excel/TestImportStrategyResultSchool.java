package com.github.chenjianhua.excel.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.Cell;
import com.github.chenjianhua.common.excel.annotation.ImportStrategy;
import com.github.chenjianhua.common.excel.bo.ipt.ImportDataBo;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractModelAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.eventlistener.AbstractModelResultAnalysisEventListener;
import com.github.chenjianhua.common.excel.support.template.AbstractImportRowTemplate;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.excel.model.SchoolModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/3/25
 */
@Slf4j
@Component
@ImportStrategy(strategyCode = "test_importResultSchool")
public class TestImportStrategyResultSchool extends AbstractImportRowTemplate {
    public TestImportStrategyResultSchool() {
        this.setSheetName("学校");
        this.setHeadRowNumber(0);
    }

    @Override
    public AbstractModelAnalysisEventListener<Map<Integer, String>> defaultAnalysisListener(ImportDataBo importDataBo) {
        SchoolModel schoolModel = new SchoolModel();
        return new AbstractModelResultAnalysisEventListener<Map<Integer, String>>(false) {

            @Override
            public void processData(Map<Integer, String> data, AnalysisContext context) {
                // 获取行的索引
                int index = context.readRowHolder().getRowIndex();
                // 获取该行的map数据
                Map<Integer, Cell> map = context.readRowHolder().getCellMap();
                if (index == 0) {
                    schoolModel.setSchoolName(map.get(1).toString());
                }
                if (index == 1) {
                    schoolModel.setUniversityMajor(map.get(1).toString());
                }
                if (index == 2) {
                    schoolModel.setName(map.get(1).toString());
                }
            }

            @Override
            public void doAfterAllProcessData(List<Map<Integer, String>> rowDatas) {
                importDataBo.getImportTaskParam().getImportArg().put("schoolModel", schoolModel);
                log.info("schoolModel:{}", JsonUtil.toJsonString(schoolModel));
            }
        };
    }
}
