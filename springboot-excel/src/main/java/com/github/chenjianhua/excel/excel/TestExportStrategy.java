package com.github.chenjianhua.excel.excel;

import com.github.chenjianhua.common.excel.annotation.ExportStrategy;
import com.github.chenjianhua.common.excel.bo.ept.ExportTaskMeta;
import com.github.chenjianhua.common.excel.example.TestExportParam;
import com.github.chenjianhua.common.excel.support.ept.AbstractExcelExport;
import com.github.chenjianhua.excel.model.TestExportModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2020/12/23
 */
@Slf4j
@Component
@ExportStrategy(strategyCode = "TestExport")
public class TestExportStrategy extends AbstractExcelExport<TestExportModel, TestExportParam> {

    public TestExportStrategy() {
        this.setSheetName("复杂导出");
        this.setFileName("复杂导出");
    }

    /**
     * 初始化数据,比如异步情况下通过token获取用户信息
     */
    @Override
    public void initExportData(ExportTaskMeta meta) {
        meta.getAuthToken();
    }

    /**
     * 查询导出数据
     */
    @Override
    protected List<TestExportModel> findExportData(TestExportParam param) {
        List<TestExportModel> rows = new LinkedList<>();
        TestExportModel testModel = new TestExportModel();
        testModel.setId(1);
        testModel.setBookName("爱的艺术");
        testModel.setBookPrice("19");
        testModel.setDesc("爱的艺术描述");
        rows.add(testModel);
        testModel = new TestExportModel();
        testModel.setId(2);
        testModel.setBookName("人性的弱点");
        testModel.setBookPrice("20");
        testModel.setDesc("人性的弱点描述");
        return rows;
    }
}

