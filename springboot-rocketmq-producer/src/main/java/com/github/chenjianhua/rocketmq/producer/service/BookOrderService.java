package com.github.chenjianhua.rocketmq.producer.service;

import com.github.chenjianhua.common.id.util.SnowFlakeStrUtil;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.common.mybatisplus.support.AbstractService;
import com.github.chenjianhua.rocketmq.producer.mapper.BookOrderMapper;
import com.github.chenjianhua.rocketmq.producer.model.BookOrder;
import com.github.chenjianhua.rocketmq.producer.param.CreateBookOrderParam;
import com.github.common.config.exception.BussinessException;
import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author chenjianhua
 * @date 2021/6/26
 */
@Slf4j
@Service
public class BookOrderService extends AbstractService<BookOrderMapper, BookOrder> {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Transactional(rollbackFor = Exception.class)
    public void createBookOrder(@RequestBody CreateBookOrderParam param) {
        BookOrder bookOrder = new BookOrder();
        bookOrder.setOrderNumber(SnowFlakeStrUtil.getSnowFlakeStr("Book"));
        bookOrder.setBookName(param.getBookName());
        bookOrder.setTradeNum(param.getTradeNum());
        log.info("保存book订单:{}", JsonUtil.toJsonString(bookOrder));
        baseMapper.insert(bookOrder);
        try {
            // 创建消息，并指定Topic，Tag和消息体
            Message msg = new Message("orderTopic", "book",
                    JsonUtil.toJsonString(bookOrder).getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
            // 发送消息到一个Broker
            SendResult sendResult = defaultMQProducer.send(msg);
            log.info("发送状态:{}", JsonUtil.toJsonString(sendResult.getSendStatus()));
        } catch (Exception e) {
            log.info("发送消息失败:{}", e);
            throw new BussinessException("发送消息失败");
        }

        //模拟补尝
        int result = 1 / 0;
    }
}
