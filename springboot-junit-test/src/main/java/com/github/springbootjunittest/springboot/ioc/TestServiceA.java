package com.github.springbootjunittest.springboot.ioc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenjianhua
 * @date 2021/5/10
 */
@Getter
@Service
public class TestServiceA {
    @Autowired
    private TestServiceB testServiceB;

}
