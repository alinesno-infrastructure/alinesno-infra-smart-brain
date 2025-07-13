package com.alinesno.infra.smart.assistant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 */
@Configuration
@EnableConfigurationProperties(ChatThreadPoolProperties.class)
public class ChatThreadPoolConfig {

    @Autowired
    private ChatThreadPoolProperties properties;

    @Bean(name = "chatThreadPool")
    public ThreadPoolTaskExecutor chatThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setWaitForTasksToCompleteOnShutdown(properties.isWaitForTasksOnShutdown());
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());

        // 动态选择拒绝策略
        executor.setRejectedExecutionHandler(getRejectedExecutionHandler());

        executor.initialize();
        return executor;
    }

    private RejectedExecutionHandler getRejectedExecutionHandler() {
        return switch (properties.getRejectedExecutionType().toUpperCase()) {
            case "ABORT" -> new ThreadPoolExecutor.AbortPolicy();
            case "DISCARD" -> new ThreadPoolExecutor.DiscardPolicy();
            case "DISCARD_OLDEST" -> new ThreadPoolExecutor.DiscardOldestPolicy();
            default -> new ThreadPoolExecutor.CallerRunsPolicy();
        };
    }
}