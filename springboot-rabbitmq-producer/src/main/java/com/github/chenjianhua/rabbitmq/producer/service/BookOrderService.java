package com.github.chenjianhua.rabbitmq.producer.service;

import com.github.chenjianhua.common.id.util.SnowFlakeStrUtil;
import com.github.chenjianhua.common.id.util.UuidUtil;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.chenjianhua.common.mybatisplus.support.AbstractService;
import com.github.chenjianhua.rabbitmq.producer.mapper.BookOrderMapper;
import com.github.chenjianhua.rabbitmq.producer.model.BookOrder;
import com.github.chenjianhua.rabbitmq.producer.param.CreateBookOrderParam;
import com.github.common.config.exception.BussinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjianhua
 * @date 2021/6/26
 */
@Slf4j
@Service
public class BookOrderService extends AbstractService<BookOrderMapper, BookOrder> implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {
    private static final String EXCHANGE_NAME = "orderDirect";
    private static final String ROUTING_KEY = "book";

    @Resource
    private RabbitTemplate template;

    @Transactional(rollbackFor = Exception.class)
    public void createBookOrder(@RequestBody CreateBookOrderParam para) {
        BookOrder bookOrder = new BookOrder();
        bookOrder.setOrderNumber(SnowFlakeStrUtil.getSnowFlakeStr("Book"));
        bookOrder.setBookName("rabbitmq事务");
        bookOrder.setTradeNum(1);
        log.info("保存book订单:{}", JsonUtil.toJsonString(bookOrder));
        baseMapper.insert(bookOrder);
        // 生产者发送消息的时候需要设置消息id
        Message message = MessageBuilder.withBody(JsonUtil.toByte(bookOrder))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("utf-8")
                .setMessageId(UuidUtil.getUUID32()).build();
        //回调
        // 如果mandatory为false，当消息无法被路由时，RabbitMQ会直接丢弃该消息。
        // mandatory为true，消息无法被路由时，RabbitMQ会调用Basic.Return命令将消息返回给生产者
        this.template.setMandatory(true);
        this.template.setConfirmCallback(this);
        this.template.setReturnsCallback(this);
        ReturnedMessage returned = new ReturnedMessage(message, 1, "成功", EXCHANGE_NAME, ROUTING_KEY);
        //构建回调返回的数据（消息ID）
        CorrelationData correlationData = new CorrelationData(bookOrder.getOrderNumber());
        correlationData.setReturned(returned);
        template.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message, correlationData);

        //模拟补尝
        int result = 1 / 0;
    }

    /**
     * mysql innodb行级锁时才会事务超时
     * 新增的时候不会超时
     * spring事务的超时时间 = 事务中最后一条sql执行完毕的时间 - 事务开始时间
     */
    @Transactional(rollbackFor = Exception.class)
    public void mysqlTimeOut() {
        log.info("mysql事务开始");
        BookOrder bookOrder = new BookOrder();
        bookOrder.setOrderNumber(SnowFlakeStrUtil.getSnowFlakeStr("Book"));
        bookOrder.setBookName("mysql事务超时");
        bookOrder.setTradeNum(1);
        baseMapper.insert(bookOrder);
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            throw new BussinessException("线程睡眠失败");
        }
        List<BookOrder> bookOrders = lambdaQuery().list();

        log.info("保存book订单:{}", JsonUtil.toJsonString(bookOrder));
    }

    /**
     * 消息到达Broker
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("消息到达Broker{} {} {}", correlationData.toString(), ack, cause);
        if (ack) {
            log.info("消息到达Broker成功");
        } else {
            log.info("消息到达Broker失败");
        }
    }

    /**
     * 消息到达Queue
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("消息到达Queue{}", JsonUtil.toJsonString(returnedMessage));
    }
}
