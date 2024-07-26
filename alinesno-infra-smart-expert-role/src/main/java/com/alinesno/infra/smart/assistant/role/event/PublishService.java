package com.alinesno.infra.smart.assistant.role.event;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class PublishService implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher ;
    
    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }
 
    public void publishEvent(Object event){
        publisher.publishEvent(event);
    }
}