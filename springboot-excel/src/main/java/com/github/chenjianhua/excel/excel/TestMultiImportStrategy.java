package com.github.chenjianhua.excel.excel;

import com.github.chenjianhua.common.excel.annotation.ImportStrategy;
import com.github.chenjianhua.common.excel.support.template.AbstractImportMultiTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author chenjianhua
 * @date 2021/3/25
 */
@Slf4j
@Component
@ImportStrategy(strategyCode = "test_multi_import")
public class TestMultiImportStrategy extends AbstractImportMultiTemplate {
    public TestMultiImportStrategy() {
        super(Arrays.asList("test_importResultSchool", "test_importResult"));
    }
}
