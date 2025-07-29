package com.alinesno.infra.smart.assistant.point.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alinesno.point")
public class PointProperties {
    private boolean enabled = false;
    private String serviceUrl;
    private long cacheDuration = 300;
}