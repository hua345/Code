package com.github.chenjianhua.springboot.jdbc.mybatis.service;

import com.github.chenjianhua.springboot.jdbc.mybatis.mapper.BookMapper;
import com.github.chenjianhua.springboot.jdbc.mybatis.model.Book;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenjianhua
 * @date 2020/9/7
 */
@Slf4j
@Service
public class BookMybatisService {
    @Autowired
    private BookMapper bookMapper;

    public int mybatisSave(Book book) {
        return bookMapper.insert(book);
    }

    public Book mybatisFindById(Long id) {
        return bookMapper.selectByPrimaryKey(id);
    }

    /**
     * PageHelper 方法使用了静态的 ThreadLocal 参数，分页参数和线程是绑定的。
     * 只要你可以保证在 PageHelper 方法调用后紧跟 MyBatis 查询方法，这就是安全的。
     */
    public PageInfo<Book> mybatisPage(String bookName, int pageNum, int pageSize, String orderBy) {
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Book> books = bookMapper.selectAllBook(bookName);
        PageInfo<Book> pageInfo = new PageInfo<>(books);
        return pageInfo;
    }
}
