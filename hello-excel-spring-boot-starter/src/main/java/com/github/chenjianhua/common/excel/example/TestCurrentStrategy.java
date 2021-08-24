package com.github.chenjianhua.common.excel.example;

import com.github.chenjianhua.common.excel.bo.BeginAndEndTimeBo;
import com.github.chenjianhua.common.excel.bo.ept.ExportTaskMeta;
import com.github.chenjianhua.common.excel.support.ept.AbstractComplexExcelExport;
import com.github.chenjianhua.common.excel.util.ExcelSplitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenjianhua
 * @date 2020/12/23
 */
@Slf4j
@Component
public class TestCurrentStrategy extends AbstractComplexExcelExport<TestCurrentModel, TestExportParam> {

    @Resource
    private TestService testService;

    public TestCurrentStrategy() {
        this.setSheetName("大数据导出测试");
        this.setFileName("大数据导出测试");
    }

    /**
     * 初始化数据,比如异步情况下通过token获取用户信息
     */
    @Override
    public void initExportData(ExportTaskMeta meta) {
        meta.getAuthToken();
    }

    @Override
    protected List<TestExportParam> buildComplexExportParam(TestExportParam param) {
        Long exportCount = testService.countData(param);
        List<BeginAndEndTimeBo> beginAndEndTimeBos = ExcelSplitUtil.splitByHour(exportCount, param.getStartTradeTime(), param.getEndTradeTime());
        List<TestExportParam> params = beginAndEndTimeBos.stream().map(item -> {
            TestExportParam testExportParam = new TestExportParam();
            BeanUtils.copyProperties(param, testExportParam);
            testExportParam.setStartTradeTime(item.getStartLocalDateTime());
            testExportParam.setEndTradeTime(item.getEndLocalDateTime());
            return testExportParam;
        }).collect(Collectors.toList());
        return params;
    }

    /**
     * 查询导出数据
     */
    @Override
    protected List<TestCurrentModel> findExportData(TestExportParam param) {
        List<TestModel> list = testService.findTestData(param);
        if (list == null) {
            list = Collections.emptyList();
        }
        List<TestCurrentModel> rows = list.stream().map(item -> {
            TestCurrentModel model = new TestCurrentModel();
            BeanUtils.copyProperties(item, model);
            return model;
        }).collect(Collectors.toList());
        return rows;
    }
}

