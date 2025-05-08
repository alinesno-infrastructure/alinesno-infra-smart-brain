package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

// 事件发布服务类
@Service
public class EventPublisherService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(EventBean eventBean) {
        CustomEvent customEvent = new CustomEvent(this, eventBean);
        applicationEventPublisher.publishEvent(customEvent);
    }
}    