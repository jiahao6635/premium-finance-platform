package com.company.platform.infra.job.handler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "platform.job.xxl", name = "enabled", havingValue = "true")
public class DemoXxlJobHandler {
    private static final Logger log = LoggerFactory.getLogger(DemoXxlJobHandler.class);

    @XxlJob("demoXxlJobHandler")
    public ReturnT<String> execute(String param) {
        log.info("XXL job triggered with param={}", param);
        return ReturnT.SUCCESS;
    }
}
