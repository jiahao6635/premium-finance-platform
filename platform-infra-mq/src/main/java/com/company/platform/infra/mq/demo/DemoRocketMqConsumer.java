package com.company.platform.infra.mq.demo;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "platform.mq.rocket", name = "enabled", havingValue = "true")
@RocketMQMessageListener(
    topic = "${platform.mq.rocket.demo-topic:platform.demo.topic}",
    consumerGroup = "${platform.mq.rocket.consumer-group:platform-demo-consumer}"
)
public class DemoRocketMqConsumer implements RocketMQListener<String> {
    private static final Logger log = LoggerFactory.getLogger(DemoRocketMqConsumer.class);

    @Override
    public void onMessage(String message) {
        log.info("RocketMQ demo message consumed: {}", message);
    }
}
