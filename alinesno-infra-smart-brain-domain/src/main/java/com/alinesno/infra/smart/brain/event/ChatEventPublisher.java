package com.alinesno.infra.smart.brain.event;

import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ChatEventPublisher {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishEvent(GenerateTaskEntity entity) {
        eventPublisher.publishEvent(entity);
    }
}
