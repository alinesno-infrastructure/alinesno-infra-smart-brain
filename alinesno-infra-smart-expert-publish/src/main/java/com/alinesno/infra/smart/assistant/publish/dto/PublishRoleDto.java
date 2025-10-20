package com.alinesno.infra.smart.assistant.publish.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分享的角色信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PublishRoleDto extends BaseDto{

    private String roleAvatar; // 角色头像

    private String roleName; // 角色名称

    private String backstory ; // 角色背景

    private String chainId; // 其他角色相关字段

    private String greeting ; // 开场白

    private String greetingQuestion ; // 开场白问题

    private String roleType ; // 角色类型

    private String responsibilities; // 角色职责描述

    private String roleLevel; // 角色等级

    private String scriptType ; // 执行类型(脚本script|流程flow|默认default)

    private boolean longTermMemoryEnabled;

    private boolean contextEngineeringEnable ;

    private boolean voiceInputStatus;

    private boolean guessWhatYouAskStatus ;

    private boolean voicePlayStatus ;

    private boolean uploadStatus ;

    private boolean welcomeConfigStatus ;

    private String welcomeConfigData ;

}
