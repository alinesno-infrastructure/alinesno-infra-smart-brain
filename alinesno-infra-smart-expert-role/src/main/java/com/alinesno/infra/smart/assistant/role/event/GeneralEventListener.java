package com.alinesno.infra.smart.assistant.role.event;

import com.alinesno.infra.smart.assistant.entity.MessageQueueEntity;
import com.alinesno.infra.smart.assistant.service.IMessageQueueService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeneralEventListener implements ApplicationListener<BaseEvent> {

    @Autowired
    private IMessageQueueService messageQueueService;

    @Override
    public void onApplicationEvent(@NotNull BaseEvent event) {

        log.debug("GeneralEventListener name = {}" ,event.getClass().getSimpleName());
        log.debug(Thread.currentThread().getName()+" , GeneralEventListener userName="+event.getSource());

        MessageQueueEntity messageQueue = (MessageQueueEntity) event.getSource() ;

        messageQueueService.updateAssistantContent(messageQueue.getBusinessId() , messageQueue.getAssistantContent());
    }
}