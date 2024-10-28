package com.alinesno.infra.smart.im.event;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 流消息传播
 */
@Getter
public class StreamMessageEvent extends ApplicationEvent {

    private final String message; // 消息内容
    private final IndustryRoleEntity role ;
    private final MessageTaskInfo taskInfo ;
    private final long bId;

    public StreamMessageEvent(Object source, String message , IndustryRoleEntity role , MessageTaskInfo taskInfo, long bId) {
        super(source);
        this.message = message;
        this.role = role ;
        this.taskInfo = taskInfo ;
        this.bId = bId;
    }

}
