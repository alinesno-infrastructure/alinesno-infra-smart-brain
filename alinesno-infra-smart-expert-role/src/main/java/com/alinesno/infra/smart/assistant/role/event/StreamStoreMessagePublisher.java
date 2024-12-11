package com.alinesno.infra.smart.assistant.role.event;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.event.StreamMessageEvent;
import com.alinesno.infra.smart.im.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class StreamStoreMessagePublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private IMessageService messageService ;

    public void doStuffAndPublishAnEvent(final String message , IndustryRoleEntity role , MessageTaskInfo taskInfo, long bId) {
        System.out.println("LLM Stream Message Publishing custom event. ");
        StreamMessageEvent customEvent = new StreamMessageEvent(this, message , role , taskInfo , bId);
        applicationEventPublisher.publishEvent(customEvent);

        // 保存消息

        MessageEntity entity = new MessageEntity();

        entity.setTraceBusId(taskInfo.getTraceBusId());

        entity.setContent(message) ;
        entity.setFormatContent(message);
        entity.setName(role.getRoleName());

        entity.setRoleType("agent");
        entity.setReaderType("html");

        entity.setAddTime(new Date());
        entity.setIcon(role.getRoleAvatar());

        entity.setAccountId(taskInfo.getAccountId());
        entity.setChannelId(taskInfo.getChannelId());
        entity.setRoleId(role.getId()) ;

        messageService.save(entity);

    }

}
