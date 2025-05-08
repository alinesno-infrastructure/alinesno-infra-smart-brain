package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 自定义事件类，继承 ApplicationEvent
 */
@Getter
public class CustomEvent extends ApplicationEvent {

    public CustomEvent(Object source, EventBean eventBean) {
        super(source);
        this.eventBean = eventBean;
    }

    private EventBean eventBean;

    public void setEventBean(EventBean eventBean) {
        this.eventBean = eventBean;
    }
}    