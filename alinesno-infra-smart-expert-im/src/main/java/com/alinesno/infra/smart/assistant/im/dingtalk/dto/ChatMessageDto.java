package com.alinesno.infra.smart.assistant.im.dingtalk.dto;

import lombok.Data;

/**
 * 消息实体信息
 */
@Data
public class ChatMessageDto {

    private String roleType;
    private String name;
    private String dateTime ;
    private String chatText;

}
