package com.alinesno.infra.smart.assistant.api;

import lombok.Data;

/**
 * 角色问题建议
 */
@Data
public class RoleQuestionSuggestionDto {
    private long roleId ;
    private long channelId;
}