package com.alinesno.infra.smart.assistant.role.event;

import com.alinesno.infra.smart.assistant.entity.MessageQueueEntity;
import lombok.Getter;

@Getter
public class MessageQueueEvent extends BaseEvent {

    private final MessageQueueEntity messageQueue ;

    public MessageQueueEvent(MessageQueueEntity messageQueue){
        super(messageQueue);
        this.messageQueue = messageQueue ;
    }


}