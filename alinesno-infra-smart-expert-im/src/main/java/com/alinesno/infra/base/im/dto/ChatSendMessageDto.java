package com.alinesno.infra.base.im.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChatSendMessageDto implements Serializable {

    private String message;
    private List<Long> users;
    private Long channelId;
    private String type;

}
