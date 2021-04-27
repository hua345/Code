package com.github.chenjianhua.springboot.mybatis.jpa;

import com.github.chenjianhua.common.id.leaf.IdLeafRedisService;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.springboot.mybatis.jpa.model.Book;
import com.github.chenjianhua.springboot.mybatis.jpa.service.BookMybatisService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

/**
 * @author chenjianhua
 * @date 2020/9/7
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("dev")
public class MybatisTest {
    @Autowired
    private IdLeafRedisService idLeafRedisService;

    @Autowired
    private BookMybatisService bookMybatisService;

    @Test
    public void MybatisTest() {
        Book book = new Book();
        book.setId(idLeafRedisService.getIdByBizTag("Book"));
        book.setBookName("刻意练习");
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());
        log.info("mybatis保存book:{}", JsonUtil.toJsonString(book));
        bookMybatisService.mybatisSave(book);
        Book bookResult = bookMybatisService.mybatisFindById(book.getId());
        log.info("mybatis查询结果book:{}", JsonUtil.toJsonString(bookResult));
    }

    @Test
    public void MybatisPageTest() {
        //排序字段 空格 排序方式,排序字段 空格 排序方式
        String orderBy = "id desc";
        PageInfo<Book> bookPage = bookMybatisService.mybatisPage("刻意练习", 2, 3, orderBy);
        log.info("mybatis 分页信息:{}", JsonUtil.toJsonString(bookPage));
        bookPage = bookMybatisService.mybatisPage("", 2, 3, orderBy);
        log.info("mybatis 分页信息:{}", JsonUtil.toJsonString(bookPage));
    }
}
