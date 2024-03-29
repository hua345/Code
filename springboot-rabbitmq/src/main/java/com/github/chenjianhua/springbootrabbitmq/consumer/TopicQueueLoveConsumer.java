package com.github.chenjianhua.springbootrabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author chenjianhua
 * @date 2021/4/9
 */
@Slf4j
@Component
public class TopicQueueLoveConsumer {
    private static final String EXCHANGE_NAME = "fangTopic";
    private static final String QUEUE_NAME = "loveTopicQue";
    private static final String BINDING_KEY = "love.#";

    /**
     * queue 不存在
     * 通常这种场景下，是需要我们来主动创建 Queue，并建立与 Exchange 的绑定关系
     * value: @Queue 注解，用于声明队列，value 为 queueName, durable 表示队列是否持久化, autoDelete 表示没有消费者之后队列是否自动删除
     * exchange: @Exchange 注解，用于声明 exchange， type 指定消息投递策略
     * key: 在 topic 方式下，这个就是我们熟知的 routingKey
     * topic通配符有两个：
     * 　　1. # ：代表没有或一个或多个单词（单词与单词之间用“.”分割）；
     * 　　2. * ：代表一个零个或一个单词；
     * 例如：aa.#.bb.*
     * 　　匹配的： aa.HH.CC.bb 、 aa.bb 、aa.bb.cc
     * 　　不匹配： aa.cc  、 aa.bb.cc.dd
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = QUEUE_NAME, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = BINDING_KEY))
    public void process(Message message) {
        try {
            String originalMsg = new String(message.getBody());
            log.info("{}收到消息:{}", QUEUE_NAME, originalMsg);
            // 处理消息
        } catch (Exception e) {
            // 记录失败日志
        }
    }
}
