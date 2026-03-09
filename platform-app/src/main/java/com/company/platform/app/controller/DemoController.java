package com.company.platform.app.controller;

import com.company.platform.common.api.ApiResponse;
import com.company.platform.infra.mq.demo.DemoRocketMqProducer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final ObjectProvider<DemoRocketMqProducer> producerProvider;

    public DemoController(ObjectProvider<DemoRocketMqProducer> producerProvider) {
        this.producerProvider = producerProvider;
    }

    @GetMapping("/secure-ping")
    @PreAuthorize("hasAuthority('user:read')")
    public ApiResponse<String> securePing() {
        return ApiResponse.success("pong");
    }

    @PostMapping("/mq/send")
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<String> sendMq(@RequestParam String message) {
        DemoRocketMqProducer producer = producerProvider.getIfAvailable();
        if (producer == null) {
            return ApiResponse.failure("MQ_DISABLED", "RocketMQ integration is disabled");
        }
        producer.send(message);
        return ApiResponse.success("sent");
    }
}
