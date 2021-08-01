package com.github.chenjianhua.springboot.jdbc.mybatisplus.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenjianhua
 * @date 2021/7/30
 */
@Service
public class TransactionalTestService {
    @Autowired
    private BookMybatisPlusService bookMybatisPlusService;

    /**
     * 在同一个包内，新建调用对象，进行访问
     */
    public void protectClassMethod() {
        //调用@Transactional标注的默认访问符方法
        bookMybatisPlusService.protectClassMethod();
    }
}
