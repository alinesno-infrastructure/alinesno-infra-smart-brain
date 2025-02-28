package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 表示消息任务的信息的数据传输对象
 * 该类封装了消息任务的相关信息，包括渠道ID、业务ID、角色ID、消息文本等
 */
@NoArgsConstructor
@ToString
@Data
public class MessageTaskInfo implements Serializable {

    // 消息渠道的唯一标识
    private long channelId ;

    // 场景的唯一标识
    private long screenId ;

    // 业务的唯一标识
    private String businessId ;

    // 角色的唯一标识
    private long roleId ;

    // 推理的文本内容
    private String reasoningText ;

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

    // 是否修改整个业务
    private boolean modifyPreBusinessId = true;

    // 消息类型
    private String roleType ;

    // 用户业务流程过程中记录的id
    private long traceBusId ; // 业务跟踪的ID

    // 当前用户信息
    private Long accountId = 0L ; // 操作账号ID
    private String accountName; // 操作账号名称
    private String accountIcon ; // 操作账号头像

    // 内容引用文章列表
    private List<MessageReferenceDto> contentReferenceArticle;

   // 消息内额外参数
    private Map<String , Object> params;

    public MessageTaskInfo(long channelId, String businessId, long roleId, String text, String preBusinessId, IndustryRoleEntity roleDto) {
        this.channelId = channelId;
        this.businessId = businessId;
        this.roleId = roleId;
        this.text = text;
        this.preBusinessId = preBusinessId;
        this.roleDto = roleDto;
    }

    /**
     * 判断是否为场景
     * @return
     */
    public boolean isScreen(){
        // 判断screenId是否大于0
       return this.screenId > 0 ;
    }
}
