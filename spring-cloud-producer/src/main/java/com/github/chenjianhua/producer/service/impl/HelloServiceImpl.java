package com.github.chenjianhua.producer.service.impl;

import com.github.chenjianhua.producer.service.HelloService;
import com.github.chenjianhua.producer.vo.HelloParam;
import com.github.common.config.exception.BussinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author chenjianhua
 * @date 2021/8/18
 */
@Slf4j
@Service
public class HelloServiceImpl implements HelloService {
    /**
     * 测试post请求
     *
     * @param param 请求参数
     * @return 测试结果
     */
    @Override
    public String postHello(HelloParam param) {
        StringBuilder sb = new StringBuilder();
        sb.append("hello ").append(param.getName()).append(" from post");
        return sb.toString();
    }

    /**
     * 测试异常
     */
    @Override
    public void testBussinessException() {
        throw new BussinessException("测试异常");
    }

    /**
     * 测试异常
     */
    @Override
    public void testException() throws Exception {
        throw new Exception("测试异常");
    }
}
