package com.company.platform.infra.job.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(XxlJobProperties.class)
@ConditionalOnProperty(prefix = "platform.job.xxl", name = "enabled", havingValue = "true")
public class XxlJobAutoConfiguration {

    @Bean
    public HealthIndicator xxlJobHealthIndicator(XxlJobProperties properties) {
        return () -> Health.up()
            .withDetail("enabled", true)
            .withDetail("adminAddresses", properties.getAdminAddresses())
            .build();
    }
}
