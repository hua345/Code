package com.github.chenjianhua.hellospringbootstarter.controller;

import com.github.chenjianhua.hellospringbootstarter.service.HelloService;
import com.github.common.resp.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/8/24
 */
@Slf4j
@Controller
@RequestMapping("/helloStarterController")
public class HelloController {

    private HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @ResponseBody
    @RequestMapping(
            method = {RequestMethod.GET}
    )
    public ResponseVO getHello(HttpServletRequest request, Map<String, Object> model) {
        log.info("Controller RequestURL :{}", request.getRequestURL().toString());
        return ResponseVO.ok(helloService.hello());
    }

    @ResponseBody
    @RequestMapping(
            method = {RequestMethod.POST}
    )
    public ResponseVO postHello(HttpServletRequest request, Map<String, Object> model) {
        log.info("Controller RequestURL :{}", request.getRequestURL().toString());
        return ResponseVO.ok(helloService.hello());
    }

    @ResponseBody
    @PostMapping(value = "/postMapping")
    public ResponseVO postMapping(HttpServletRequest request, Map<String, Object> model) {
        log.info("Controller RequestURL :{}", request.getRequestURL().toString());
        return ResponseVO.ok(helloService.hello());
    }
}
