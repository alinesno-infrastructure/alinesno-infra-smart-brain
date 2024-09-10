package com.alinesno.infra.base.im.event;

import com.alinesno.infra.base.im.dto.ChatMessageDto;
import com.alinesno.infra.base.im.service.ISSEService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SSEMessageEventListener {

    @Autowired
    private ISSEService sseService;

    @SneakyThrows
    @EventListener
    public void handleCustomEvent(SSEMessageEvent event) {

        ChatMessageDto message = event.getMessage();
        sseService.send("20" , message);
    }
}