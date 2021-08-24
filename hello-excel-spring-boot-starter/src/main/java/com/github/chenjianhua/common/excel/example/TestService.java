package com.github.chenjianhua.common.excel.example;


import com.github.chenjianhua.common.excel.bo.TableFieldInfoBo;
import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenjianhua
 * @date 2020/12/24
 */
@Slf4j
@Service
public class TestService {
    private AtomicInteger testData = new AtomicInteger(1);

    public Long countData(TestExportParam param) {
        log.info("统计数据总数 param:{}", JsonUtil.toJsonString(param));
        return 1000L;
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
        List<TestModel> testModels = new ArrayList<>(154);
        for (int i = 1; i <= 200; i++) {
            TestModel testModel = new TestModel();
            testModel.setBookName("数学之美" + testData.get());
            testModel.setBookPrice(BigDecimal.valueOf(testData.get()));
            testModels.add(testModel);
        }
        testData.incrementAndGet();
        return testModels;
    }
}
