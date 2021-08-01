package com.github.chenjianhua.springboot.jdbc.controller;

import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.springboot.jdbc.param.TestEnumParam;
import com.github.common.enums.SexEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenjianhua
 * @date 2020/12/1
 */
@Slf4j
@RestController
public class Controller {
    @PostMapping("/fruit")
    public TestEnumParam testEnum(@RequestBody TestEnumParam param) {
        log.info("param :{}", JsonUtil.toJsonString(param));
        return param;
    }

    @PostMapping("/fruit2")
    public SexEnum testEnum2(@RequestParam SexEnum param) {
        log.info("param :{}", JsonUtil.toJsonString(param));
        return param;
    }
}
