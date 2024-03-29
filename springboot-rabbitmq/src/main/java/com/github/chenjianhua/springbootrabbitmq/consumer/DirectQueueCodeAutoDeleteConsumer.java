package com.github.chenjianhua.springbootrabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
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
public class DirectQueueCodeAutoDeleteConsumer {
    private static final String EXCHANGE_NAME = "fangDirect";
    private static final String QUEUE_NAME = "fangDirectQueCodeAutoDelete";
    private static final String BINDING_KEY = "love";

    /**
     * queue 不存在
     * 通常这种场景下，是需要我们来主动创建 Queue，并建立与 Exchange 的绑定关系
     * value: @Queue 注解，用于声明队列，value 为 queueName, durable 表示队列是否持久化, autoDelete 表示没有消费者之后队列是否自动删除
     * exchange: @Exchange 注解，用于声明 exchange， type 指定消息投递策略
     * key: 在 topic 方式下，这个就是我们熟知的 routingKey
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = QUEUE_NAME, durable = "false", autoDelete = "true"),
            exchange = @Exchange(value = EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
            key = BINDING_KEY))
    public void process(String message) {
        log.info("{}收到消息:{}", QUEUE_NAME, message);
    }
}
