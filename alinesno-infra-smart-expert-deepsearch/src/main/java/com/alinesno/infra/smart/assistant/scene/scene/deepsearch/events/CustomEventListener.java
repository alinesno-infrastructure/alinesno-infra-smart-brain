package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// 自定义事件监听器
@Component
public class CustomEventListener {

    @EventListener
    public void handleCustomEvent(CustomEvent event) {
        EventBean eventBean = event.getEventBean();
        System.out.println("接收到自定义事件: " + eventBean);
    }
}    