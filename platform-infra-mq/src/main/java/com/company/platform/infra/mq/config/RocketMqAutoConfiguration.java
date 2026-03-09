package com.company.platform.infra.mq.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RocketMqProperties.class)
@ConditionalOnProperty(prefix = "platform.mq.rocket", name = "enabled", havingValue = "true")
public class RocketMqAutoConfiguration {

    @Bean
    public HealthIndicator rocketMqHealthIndicator(RocketMqProperties properties) {
        return () -> Health.up()
            .withDetail("enabled", true)
            .withDetail("topic", properties.getDemoTopic())
            .build();
    }
}
