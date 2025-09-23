package com.alinesno.infra.smart.assistant.adapter.event;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.event.StreamMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * StreamMessagePublisher
 */
@Slf4j
@Component
public class StreamMessagePublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void doStuffAndPublishAnEvent(final String message , IndustryRoleEntity role , MessageTaskInfo taskInfo, long bId) {
        try{
            StreamMessageEvent customEvent = new StreamMessageEvent(this, message , role , taskInfo , bId , null);
            applicationEventPublisher.publishEvent(customEvent);
        }catch(Exception e){
            log.error("消息通知渠道异常 error:{}" , e.getMessage());
        }
    }


    public void doStuffAndPublishAnEvent(final String message , IndustryRoleEntity role , MessageTaskInfo taskInfo, long bId , long messageId) {

        try{
            StreamMessageEvent customEvent = new StreamMessageEvent(this, message , role , taskInfo , bId , messageId);
            applicationEventPublisher.publishEvent(customEvent);
        }catch(Exception e){
            log.error("消息通知渠道异常:messageId:{}, error:{}" , messageId , e.getMessage());
        }

    }
}
