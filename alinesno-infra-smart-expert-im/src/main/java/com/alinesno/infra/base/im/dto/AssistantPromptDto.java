package com.alinesno.infra.base.im.dto;

import lombok.Data;

/**
 * 发送Agent角色消息内容
 */
@Data
public class AssistantPromptDto {

    private Long businessId;
    private long agentId;
    private String content;
    private long channelId ;
    private String assistantContent ;

}
