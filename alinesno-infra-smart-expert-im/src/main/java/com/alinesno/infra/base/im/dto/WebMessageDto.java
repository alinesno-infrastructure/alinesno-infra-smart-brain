package com.alinesno.infra.base.im.dto;

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
    private String businessId;

}