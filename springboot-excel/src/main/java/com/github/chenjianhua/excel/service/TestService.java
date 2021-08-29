package com.github.chenjianhua.excel.service;


import com.github.chenjianhua.common.excel.entity.TableFieldInfoBo;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.excel.model.TestModel;
import com.github.chenjianhua.excel.model.param.TestExportParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2020/12/24
 */
@Slf4j
@Service
public class TestService {
    private Integer testData = 1;

    public Long countData(TestExportParam param) {
        log.info("统计数据总数 param:{}", JsonUtil.toJsonString(param));
        return 10 * 10000L;
    }

    public List<TableFieldInfoBo> findTableFieldInfoBo() {
        List<TableFieldInfoBo> tableFieldInfoBos = new LinkedList<>();
        TableFieldInfoBo aa = new TableFieldInfoBo();
        aa.setFieldCode("bookName");
        aa.setFieldName("动态书名");
        tableFieldInfoBos.add(aa);
        TableFieldInfoBo bb = new TableFieldInfoBo();
        bb.setFieldCode("bookPrice");
        bb.setFieldName("动态图书价格");
        tableFieldInfoBos.add(bb);
        return tableFieldInfoBos;
    }


    public List<TestModel> findTestData(TestExportParam param) {
        log.info("查询数据 param:{}", JsonUtil.toJsonString(param));
        List<TestModel> testModels = new ArrayList<>(1000);
        for (int i = 1; i <= 1000; i++) {
            TestModel testModel = new TestModel();
            testModel.setBookName("数学之美" + testData);
            testModel.setBookPrice(testData.toString());
            testModels.add(testModel);
        }
        testData++;
        return testModels;
    }
}
