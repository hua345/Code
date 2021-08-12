package com.github.chenjianhua.springboot.jdbc;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.chenjianhua.common.id.leaf.IdLeafRedisService;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.common.mybatisplus.vo.Direction;
import com.github.chenjianhua.common.mybatisplus.vo.PageVo;
import com.github.chenjianhua.common.mybatisplus.vo.SortOrder;
import com.github.chenjianhua.springboot.jdbc.mybatisplus.model.Book;
import com.github.chenjianhua.springboot.jdbc.mybatisplus.service.BookMybatisPlusService;
import com.github.chenjianhua.springboot.jdbc.param.BookMybatisPlusParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;


/**
 * @author chenjianhua
 * @date 2020/9/7
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("dev")
public class MybatisPlusTest {
    @Autowired
    private IdLeafRedisService idLeafRedisService;

    @Autowired
    private BookMybatisPlusService bookMybatisPlusService;

    @Test
    public void MybatisPlusTest() {
        Book book = new Book();
        book.setId(idLeafRedisService.getIdByBizTag("Book"));
        book.setBookName("刻意练习");
        Assertions.assertTrue(bookMybatisPlusService.save(book));
        bookMybatisPlusService.save(book);
        Book bookResult = bookMybatisPlusService.getById(book.getId());
        Assertions.assertNotNull(bookResult);
        Assertions.assertEquals(book.getId(), bookResult.getId());
        Assertions.assertEquals(book.getBookName(), bookResult.getBookName());
    }

    @Test
    public void MybatisPlusPageTest() {
        BookMybatisPlusParam bookMybatisPlusParam = new BookMybatisPlusParam();
        bookMybatisPlusParam.setBookName("刻意练习");
        bookMybatisPlusParam.setPage(2);
        bookMybatisPlusParam.setSize(2);
        SortOrder sortOrder = new SortOrder();
        sortOrder.setSortOrder(Direction.DESC);
        sortOrder.setSortName("id");
        bookMybatisPlusParam.setOrders(Collections.singletonList(sortOrder));
        PageVo<Book> bookPage = bookMybatisPlusService.mybatisPlusPage(bookMybatisPlusParam);
        log.info("mybatisPlus 分页信息:{}", JsonUtil.toJsonString(bookPage));
        bookMybatisPlusParam.setBookName("");
        bookPage = bookMybatisPlusService.mybatisPlusPage(bookMybatisPlusParam);
        log.info("mybatisPlus 分页信息:{}", JsonUtil.toJsonString(bookPage));
        bookMybatisPlusParam.setBookName("");
        IPage<Book> bookIPage = bookMybatisPlusService.mybatisPlusIPage(bookMybatisPlusParam);
        log.info("mybatisPlus iPage 分页信息:{}", JsonUtil.toJsonString(bookIPage));
    }
}
