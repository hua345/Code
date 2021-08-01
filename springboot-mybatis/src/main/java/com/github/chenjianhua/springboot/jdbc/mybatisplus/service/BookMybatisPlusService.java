package com.github.chenjianhua.springboot.jdbc.mybatisplus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.chenjianhua.common.mybatisplus.support.AbstractService;
import com.github.chenjianhua.common.mybatisplus.vo.PageVo;
import com.github.chenjianhua.springboot.jdbc.mybatisplus.model.Book;
import com.github.chenjianhua.springboot.jdbc.mybatisplus.mapper.BookMybatisPlusMapper;
import com.github.chenjianhua.springboot.jdbc.param.BookMybatisPlusParam;
import com.github.common.config.exception.BussinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chenjianhua
 * @date 2021/4/28
 */
@Service
public class BookMybatisPlusService extends AbstractService<BookMybatisPlusMapper, Book> {
    public PageVo<Book> mybatisPlusPage(BookMybatisPlusParam param) {
        return convertPage(baseMapper.mybatisPlusPage(param.getPageable(), param.getBookName()));
    }

    public IPage<Book> mybatisPlusIPage(BookMybatisPlusParam param) {
        return baseMapper.mybatisPlusPage(param.getPageable(), param.getBookName());
    }

    /**
     * 如果Transactional注解应用在非 public 修饰的方法上，Transactional将会失效
     * 是因为在Spring AOP 代理时，TransactionInterceptor （事务拦截器）在目标方法执行前后进行拦截，
     * DynamicAdvisedInterceptor（CglibAopProxy 的内部类）的intercept方法 或 JdkDynamicAopProxy的invoke方法会
     * 间接调用AbstractFallbackTransactionAttributeSource的 computeTransactionAttribute方法，获取Transactional 注解的事务配置信息。
     */
    @Transactional(rollbackFor = Exception.class)
    void protectClassMethod() {
        Book book = new Book();
        book.setBookName("非public方法导致@Transactional失效");
        int result = baseMapper.insert(book);
        if (result > 0) {
            throw new BussinessException("非public方法导致@Transactional失效");
        }
    }

    /**
     * 同一个类中方法调用，导致@Transactional失效
     * 只有当事务方法被当前类以外的代码调用时，才会由Spring生成的代理对象来管理。
     */
    public void testSameClassMethod() {
        sameClassMethod();
    }

    @Transactional(rollbackFor = Exception.class)
    public void sameClassMethod() {
        Book book = new Book();
        book.setBookName("同一个类中方法调用，导致@Transactional失效");
        int result = baseMapper.insert(book);
        if (result > 0) {
            throw new BussinessException("同一个类中方法调用，导致@Transactional失效");
        }
    }

    /**
     * @Transactional 注解属性 rollbackFor 设置错误
     */
    @Transactional(rollbackFor = BussinessException.class)
    public void rollbackForError() throws Exception {
        Book book = new Book();
        book.setBookName("@Transactional注解属性rollbackFor设置错误");
        int result = baseMapper.insert(book);
        if (result > 0) {
            throw new Exception("@Transactional 注解属性rollbackFor设置错误");
        }
    }

    /**
     * @Transactional 注解属性 propagation 设置错误
     * TransactionDefinition.PROPAGATION_SUPPORTS：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行。
     * TransactionDefinition.PROPAGATION_NOT_SUPPORTED：以非事务方式运行，如果当前存在事务，则把当前事务挂起。
     * TransactionDefinition.PROPAGATION_NEVER：以非事务方式运行，如果当前存在事务，则抛出异常。
     */
    @Transactional(rollbackFor = BussinessException.class, propagation = Propagation.NEVER)
    public void propagationError() throws Exception {
        Book book = new Book();
        book.setBookName("@Transactional 注解属性propagation设置错误");
        int result = baseMapper.insert(book);
        if (result > 0) {
            throw new Exception("@Transactional 注解属性propagation设置错误");
        }
    }

    /**
     * 异常被catch捕获导致@Transactional失效
     */
    @Transactional(rollbackFor = Exception.class)
    public void tryCatchException() {
        try {
            Book book = new Book();
            book.setBookName("异常被catch捕获导致@Transactional失效");
            int result = baseMapper.insert(book);
            if (result > 0) {
                throw new Exception("异常被catch捕获导致@Transactional失效");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
