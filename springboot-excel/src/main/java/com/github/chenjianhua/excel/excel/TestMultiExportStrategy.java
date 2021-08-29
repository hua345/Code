package com.github.chenjianhua.excel.excel;

import com.github.chenjianhua.common.excel.annotation.ExportStrategy;
import com.github.chenjianhua.common.excel.entity.BeginAndEndTimeBo;
import com.github.chenjianhua.common.excel.entity.exportexcel.ExportTaskParam;
import com.github.chenjianhua.common.excel.support.ept.AbstractComplexExcelExport;
import com.github.chenjianhua.common.excel.util.ExcelSplitUtil;
import com.github.chenjianhua.excel.model.TestExportModel;
import com.github.chenjianhua.excel.model.TestModel;
import com.github.chenjianhua.excel.model.TestMultiExportModel;
import com.github.chenjianhua.excel.model.param.TestExportParam;
import com.github.chenjianhua.excel.service.TestService;
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
@ExportStrategy(strategyCode = "TestMultiExport")
public class TestMultiExportStrategy extends AbstractComplexExcelExport<TestMultiExportModel, TestExportParam> {

    @Resource
    private TestService testService;

    public TestMultiExportStrategy() {
        this.setSheetName("大数据导出测试");
        this.setFileName("大数据导出测试");
    }

    /**
     * 初始化数据,比如异步情况下通过token获取用户信息
     */
    @Override
    public void initExportData(ExportTaskParam meta) {
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
    protected List<TestMultiExportModel> findExportData(TestExportParam param) {
        List<TestModel> list = testService.findTestData(param);
        if (list == null) {
            list = Collections.emptyList();
        }
        List<TestMultiExportModel> rows = list.stream().map(item -> {
            TestMultiExportModel model = new TestMultiExportModel();
            BeanUtils.copyProperties(item, model);
            return model;
        }).collect(Collectors.toList());
        return rows;
    }
}

