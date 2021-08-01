package com.github.chenjianhua.springboot.jdbc;

import com.github.chenjianhua.springboot.jdbc.mybatisplus.service.BookMybatisPlusService;
import com.github.chenjianhua.springboot.jdbc.mybatisplus.service.TransactionalTestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author chenjianhua
 * @date 2021/7/30
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("dev")
public class TransactionalTest {
    @Autowired
    private BookMybatisPlusService bookMybatisPlusService;

    @Autowired
    private TransactionalTestService transactionalTestService;

    @Test
    public void testSameClassMethod() {
        bookMybatisPlusService.testSameClassMethod();
    }

    @Test
    public void testRollbackForError() throws Exception {
        bookMybatisPlusService.rollbackForError();
    }

    @Test
    public void testPropagationError() throws Exception {
        bookMybatisPlusService.propagationError();
    }

    @Test
    public void testTryCatchException() {
        bookMybatisPlusService.tryCatchException();
    }

    @Test
    public void testTransactionalTestService() {
        transactionalTestService.protectClassMethod();
    }
}
