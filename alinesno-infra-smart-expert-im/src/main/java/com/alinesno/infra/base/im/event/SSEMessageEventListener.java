package com.alinesno.infra.base.im.event;

import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.service.ISSEService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SSEMessageEventListener {

    @Autowired
    private ISSEService sseService;

    @SneakyThrows
    @EventListener
    public void handleCustomEvent(SSEMessageEvent event) {

        ChatMessageDto message = event.getMessage();
        String channelId = String.valueOf(message.getChannelId());
        try {
            sseService.send(channelId , message);
        }catch (Exception e){
            log.error("send message error:{}",e.getMessage());
        }

    }
}