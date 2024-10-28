package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 表示消息任务的信息的数据传输对象
 * 该类封装了消息任务的相关信息，包括渠道ID、业务ID、角色ID、消息文本等
 */
@NoArgsConstructor
@ToString
@Data
public class MessageTaskInfo implements Serializable {

    // 账号ID
    private long accountId ;
    // 消息渠道的唯一标识
    private long channelId ;
    // 业务的唯一标识
    private String businessId ;
    // 角色的唯一标识
    private long roleId ;
    // 消息的文本内容
    private String text ;
    // 前置业务的标识，用于关联消息任务与之前的业务
    private String preBusinessId ;
    // 角色的详细信息，使用实体类封装
    private IndustryRoleEntity roleDto ;
    // 消息任务的执行时间
    private String usageTime ;
    // 是否执行方法
    private boolean functionCall ;
    // 是否为修改内容
    private boolean modify;

    // 用户业务流程过程中记录的id
    private long workflowRecordId ; // 聊天记录的id

    public MessageTaskInfo(long channelId, String businessId, long roleId, String text, String preBusinessId, IndustryRoleEntity roleDto) {
        this.channelId = channelId;
        this.businessId = businessId;
        this.roleId = roleId;
        this.text = text;
        this.preBusinessId = preBusinessId;
        this.roleDto = roleDto;
    }
}
