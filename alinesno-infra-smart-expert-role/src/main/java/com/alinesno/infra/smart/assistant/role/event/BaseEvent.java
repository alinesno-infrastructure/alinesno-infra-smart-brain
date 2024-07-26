package com.alinesno.infra.smart.assistant.role.event;

import org.springframework.context.ApplicationEvent;

public class BaseEvent extends ApplicationEvent {

    public BaseEvent(Object source) {
        super(source);
    }

}