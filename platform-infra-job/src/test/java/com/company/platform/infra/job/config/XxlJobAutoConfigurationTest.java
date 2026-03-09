package com.company.platform.infra.job.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class XxlJobAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(XxlJobAutoConfiguration.class);

    @Test
    void shouldNotCreateBeanWhenDisabled() {
        contextRunner
            .withPropertyValues("platform.job.xxl.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean("xxlJobHealthIndicator"));
    }

    @Test
    void shouldCreateBeanWhenEnabled() {
        contextRunner
            .withPropertyValues("platform.job.xxl.enabled=true")
            .run(context -> assertThat(context).hasBean("xxlJobHealthIndicator"));
    }
}
