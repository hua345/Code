package com.github.chenjianhua.springbootrocketmq.config;

import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/6/27
 */
@Slf4j
@Component
public class MqMessageListenerProcessor implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if (CollectionUtils.isEmpty(list)) {
            log.info("收到的消息为空");
        } else {
            log.info("收到了消息{}", JsonUtil.toJsonString(list));
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}