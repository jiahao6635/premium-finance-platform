package com.company.platform.infra.mq.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class RocketMqAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(RocketMqAutoConfiguration.class);

    @Test
    void shouldNotCreateBeanWhenDisabled() {
        contextRunner
            .withPropertyValues("platform.mq.rocket.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean("rocketMqHealthIndicator"));
    }

    @Test
    void shouldCreateBeanWhenEnabled() {
        contextRunner
            .withPropertyValues("platform.mq.rocket.enabled=true")
            .run(context -> assertThat(context).hasBean("rocketMqHealthIndicator"));
    }
}
