package com.github.chenjianhua.producer.controller;

import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.producer.service.HelloService;
import com.github.chenjianhua.producer.vo.HelloParam;
import com.github.common.resp.ResponseVO;
import com.github.common.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenjianhua
 * @date 2021/3/12
 */
@Slf4j
@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    @GetMapping("/hello")
    public ResponseVO<String> index(@RequestParam String name) {
        log.info("get hello param:{}", name);
        StringBuilder sb = new StringBuilder();
        sb.append("hello ").append(name);
        return ResponseUtil.ok(sb.toString());
    }

    @PostMapping("/postHello")
    public ResponseVO<String> postHello(@RequestBody HelloParam param) {
        log.info("postHello param:{}", JsonUtil.toJsonString(param));
        return ResponseUtil.ok(helloService.postHello(param));
    }

    @PostMapping("/testBusinessException")
    public ResponseVO<String> testBusinessException() {
        helloService.testBussinessException();
        return ResponseUtil.ok();
    }

    @PostMapping("/testException")
    public ResponseVO<String> testException() throws Exception {
        helloService.testException();
        return ResponseUtil.ok();
    }
}