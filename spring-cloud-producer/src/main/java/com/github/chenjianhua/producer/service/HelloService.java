package com.github.chenjianhua.producer.service;

import com.github.chenjianhua.producer.vo.HelloParam;

/**
 * @author chenjianhua
 * @date 2021/8/18
 */
public interface HelloService {
    /**
     * 测试post请求
     * @param param 请求参数
     * @return 测试结果
     */
    String postHello(HelloParam param);

    /**
     * 测试异常
     */
    void testBussinessException();

    /**
     * 测试异常
     */
    void testException() throws Exception;
}
