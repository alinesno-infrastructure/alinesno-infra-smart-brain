package com.alinesno.infra.smart.brain.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ChatEventListener {
    @EventListener
    public void handleEvent(ChatEvent event) {
        String message = event.getMessage();
        // 处理事件逻辑
        System.out.println("Received event: " + message);
    }
}
