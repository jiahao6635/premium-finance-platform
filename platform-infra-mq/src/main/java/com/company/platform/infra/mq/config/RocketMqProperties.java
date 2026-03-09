package com.company.platform.infra.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "platform.mq.rocket")
public class RocketMqProperties {
    private boolean enabled = false;
    private String demoTopic = "platform.demo.topic";
    private String producerGroup = "platform-demo-producer";
    private String consumerGroup = "platform-demo-consumer";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDemoTopic() {
        return demoTopic;
    }

    public void setDemoTopic(String demoTopic) {
        this.demoTopic = demoTopic;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }
}
