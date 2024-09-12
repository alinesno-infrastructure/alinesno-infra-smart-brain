package com.alinesno.infra.base.im.event;

import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SSEMessageEventService {

    private final ApplicationEventPublisher publisher;

    @Autowired
    public SSEMessageEventService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void triggerCustomEvent(ChatMessageDto message) {
        // 触发自定义事件
        SSEMessageEvent customEvent = new SSEMessageEvent(this, message);
        this.publisher.publishEvent(customEvent);
    }
}