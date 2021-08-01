package com.github.chenjianhua.rocketmq.producer.service;

import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.common.mybatisplus.support.AbstractService;
import com.github.chenjianhua.rocketmq.producer.mapper.BookOrderTransactionMapper;
import com.github.chenjianhua.rocketmq.producer.model.BookOrder;
import com.github.chenjianhua.rocketmq.producer.model.BookOrderTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

/**
 * @author chenjianhua
 * @date 2021/7/14
 */
@Slf4j
@Service
public class BookOrderTransactionService extends AbstractService<BookOrderTransactionMapper, BookOrderTransaction> implements TransactionListener {

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        log.info("开始执行本地事务....");
        String body = new String(message.getBody());
        BookOrder bookOrder = JsonUtil.toBean(body, BookOrder.class);
        BookOrderTransaction bookOrderTransaction = new BookOrderTransaction();
        bookOrderTransaction.setOrderNumber(bookOrder.getOrderNumber());
        bookOrderTransaction.setTransactionId(message.getTransactionId());
        baseMapper.insert(bookOrderTransaction);
        log.info("本地事务已提交。{}", message.getTransactionId());
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        log.info("开始回查本地事务状态。{}", messageExt.getTransactionId());
        LocalTransactionState state;
        String transactionId = messageExt.getTransactionId();
        Integer transactionNum = lambdaQuery().eq(BookOrderTransaction::getTransactionId, transactionId).count();
        if (transactionNum > 0) {
            state = LocalTransactionState.COMMIT_MESSAGE;
        } else {
            state = LocalTransactionState.UNKNOW;
        }
        log.info("结束本地事务状态查询：{}", state);
        return state;
    }
}
