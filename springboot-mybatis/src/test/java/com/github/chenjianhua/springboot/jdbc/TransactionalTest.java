package com.github.chenjianhua.springboot.jdbc;

import com.github.chenjianhua.springboot.jdbc.config.TransactionalFailEnum;
import com.github.chenjianhua.springboot.jdbc.mybatisplus.service.BookMybatisPlusService;
import com.github.chenjianhua.springboot.jdbc.mybatisplus.service.TransactionalTestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
        Exception exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> bookMybatisPlusService.testSameClassMethod());
        Assertions.assertEquals(TransactionalFailEnum.sameClassMethod.getDescription(), exception.getMessage());
    }

    @Test
    public void testRollbackForError() {
        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> bookMybatisPlusService.rollbackForError());
        Assertions.assertEquals(TransactionalFailEnum.rollbackForError.getDescription(), exception.getMessage());

    }

    @Test
    public void testPropagationError() {
        Exception exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> bookMybatisPlusService.propagationError());
        Assertions.assertEquals(TransactionalFailEnum.propagationError.getDescription(), exception.getMessage());

    }

    @Test
    public void testTryCatchException() {
        bookMybatisPlusService.tryCatchException();
    }

    @Test
    public void testTransactionalTestService() {
        Exception exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> transactionalTestService.protectClassMethod());
        Assertions.assertEquals(TransactionalFailEnum.protectClassMethod.getDescription(), exception.getMessage());

    }
}
