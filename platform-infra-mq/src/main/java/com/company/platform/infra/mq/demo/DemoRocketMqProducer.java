package com.company.platform.infra.mq.demo;

import com.company.platform.infra.mq.config.RocketMqProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "platform.mq.rocket", name = "enabled", havingValue = "true")
public class DemoRocketMqProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final RocketMqProperties properties;

    public DemoRocketMqProducer(RocketMQTemplate rocketMQTemplate, RocketMqProperties properties) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.properties = properties;
    }

    public void send(String message) {
        rocketMQTemplate.convertAndSend(properties.getDemoTopic(), message);
    }
}
