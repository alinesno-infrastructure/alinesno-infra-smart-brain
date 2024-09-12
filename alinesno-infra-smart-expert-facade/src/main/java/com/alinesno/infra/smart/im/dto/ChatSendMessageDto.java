package com.alinesno.infra.smart.im.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChatSendMessageDto implements Serializable {

    private String message;
    private List<Long> users; // Agent用户ID列表
    private List<Long> businessIds ;  // 业务ID列表
    private Long channelId;
    private Long accountId ; // 操作账号ID
    private String type;

}
