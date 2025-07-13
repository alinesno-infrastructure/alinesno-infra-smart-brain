package com.alinesno.infra.smart.assistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 聊天线程池属性配置
 */
@Data
@ConfigurationProperties(prefix = "alinesno.thread-pool.chat")
public class ChatThreadPoolProperties {

    private int corePoolSize = 5;
    private int maxPoolSize = 20;
    private int queueCapacity = 100;
    private String threadNamePrefix = "chat-thread-";
    private int keepAliveSeconds = 60;
    private boolean waitForTasksOnShutdown = true;
    private int awaitTerminationSeconds = 30;
    private String rejectedExecutionType = "CALLER_RUNS";

}