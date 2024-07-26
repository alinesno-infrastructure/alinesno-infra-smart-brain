package com.alinesno.infra.smart.assistant.plugin;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@ConfigurationProperties(prefix = "alinesno.infra.smart.assistant.plugin")
public class AssistantPluginProperties {

    private String path ; // 插件存放路径

    private String central ; // 插件中心地址

}