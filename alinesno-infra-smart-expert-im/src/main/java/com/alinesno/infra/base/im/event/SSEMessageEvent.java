package com.alinesno.infra.base.im.event;

import com.alinesno.infra.base.im.dto.ChatMessageDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SSEMessageEvent extends ApplicationEvent {

    private final ChatMessageDto message;

    public SSEMessageEvent(Object source, ChatMessageDto message) {
        super(source);
        this.message = message;
    }

}