package com.alinesno.infra.smart.assistant.scene.scene.productResearch.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Git工具类日志事件
 */
@Getter
public class GitLogEvent extends ApplicationEvent {

    private final String logMessage; // 日志内容
    private final String logLevel;   // 日志级别（INFO/ERROR等）
    private final String sourceClass; // 日志来源类（如GitUtils）
    private final String logTime; //  日志时间

    @Setter
    private String channelStreamId; //  渠道ID

    public GitLogEvent(Object source, String logMessage, String logLevel, String sourceClass , String logTime) {
        super(source);
        this.logMessage = logMessage;
        this.logLevel = logLevel;
        this.sourceClass = sourceClass;
        this.logTime = logTime;
    }
}