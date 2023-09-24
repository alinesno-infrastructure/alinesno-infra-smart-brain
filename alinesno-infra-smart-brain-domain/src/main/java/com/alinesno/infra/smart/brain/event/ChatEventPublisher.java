package com.alinesno.infra.smart.brain.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ChatEventPublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishEvent(TaskEvent entity) {
        eventPublisher.publishEvent(entity);
    }
}
