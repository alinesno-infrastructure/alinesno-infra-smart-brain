package com.alinesno.infra.smart.assistant.adapter.event;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.event.StreamMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class StreamMessagePublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void doStuffAndPublishAnEvent(final String message , IndustryRoleEntity role , MessageTaskInfo taskInfo, long bId) {
        System.out.println("LLM Stream Message Publishing custom event. ");
        StreamMessageEvent customEvent = new StreamMessageEvent(this, message , role , taskInfo , bId);
        applicationEventPublisher.publishEvent(customEvent);
    }

}
