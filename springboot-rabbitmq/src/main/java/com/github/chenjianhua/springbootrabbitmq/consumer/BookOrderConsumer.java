package com.github.chenjianhua.springbootrabbitmq.consumer;

import com.github.common.config.exception.BussinessException;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjianhua
 * @date 2021/4/9
 */
@Slf4j
@Component
public class BookOrderConsumer {
    private static final String EXCHANGE_NAME = "orderDirect";

    private static final String QUEUE_NAME = "bookOrderQueue";
    private static final String BINDING_KEY = "book";

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = QUEUE_NAME, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = EXCHANGE_NAME, type = ExchangeTypes.DIRECT), key = BINDING_KEY), ackMode = "MANUAL")
    public void process(String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.info("{}收到消息:{}", QUEUE_NAME, message);
        // deliveryTag：表示消息投递序号，每次消费消息或者消息重新投递后，deliveryTag都会增加。手动消息确认模式下，我们可以对指定deliveryTag的消息进行ack、nack、reject等操作。
        // multiple：是否批量确认，值为 true 则会一次性 ack所有小于当前消息 deliveryTag 的消息。
        // 举个栗子： 假设我先发送三条消息deliveryTag分别是5、6、7，可它们都没有被确认，
        // 当我发第四条消息此时deliveryTag为8，multiple设置为 true，会将5、6、7、8的消息全部进行确认。
        // 第三个参数true，表示这个消息会重新进入队列
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            channel.basicNack(deliveryTag, false, true);
            throw new BussinessException("线程睡眠失败");
        }
        channel.basicNack(deliveryTag, false, true);
        // 拒绝消息，与basicNack区别在于不能进行批量操作，其他用法很相似。
        // channel.basicReject(deliveryTag, true);
    }
}
