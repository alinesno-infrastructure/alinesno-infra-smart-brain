package com.alinesno.infra.smart.assistant.point.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alinesno.point")
public class PointProperties {
    private boolean enabled = false; // 是否启用积分功能
}