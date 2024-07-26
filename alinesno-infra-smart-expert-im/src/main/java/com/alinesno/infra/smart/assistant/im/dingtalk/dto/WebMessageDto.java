package com.alinesno.infra.smart.assistant.im.dingtalk.dto;

import lombok.Data;

/**
 * 客户端发送消息
 */
@Data
public class WebMessageDto {

    private String type;
    private String username;
    private String id;
    private String text;

}