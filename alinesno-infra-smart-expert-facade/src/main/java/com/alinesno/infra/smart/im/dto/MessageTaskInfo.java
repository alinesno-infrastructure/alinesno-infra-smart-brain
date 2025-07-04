package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
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

    // 消息渠道的流ID
    private String channelStreamId ;

    // 场景的唯一标识
    private long sceneId ;

    // 业务的唯一标识
    private String businessId ;

    // 消息的唯一标识
    private long messageId ;

    // 角色的唯一标识
    private long roleId ;

    // 推理的文本内容
    private String reasoningText ;

    // 消息的文本内容
    private String fullContent ;

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

    // 用于业务流程聊天的唯一ID，做为流程聊天的唯一ID，做流程聊天的记录
    private long flowChatId ; // 流程聊天ID

    // 当前用户信息
    // 操作账号ID
    private Long accountId = 0L ;

    // 账号组织ID
    private Long accountOrgId = 0L ;

    // 当前账号用户名
    private String accountName;

    // 当前账号邮件
    private String accountEmail ;

    // 操作账号头像
    private String accountIcon ;

    // 流程运行信息
    // 当前流程节点ID
    private FlowStepStatusDto flowStep ;

    // 节点生成的内容
    private String flowStepContent ;

    // 内容引用文章列表
    private List<MessageReferenceDto> contentReferenceArticle;

    // 错误信息
    private String errorMessage;

    // 是否异常
    private boolean hasError = false ;

    // 消息内额外参数
    private Map<String , Object> params;

    // 使用消耗情况(时间/Token数)
    private MessageUsageDto usage ;

    // 是否有执行能力
    private boolean hasExecuteTool = false ;

    // 附件列表
    private List<FileAttachmentDto> attachments ;

    // 深度搜索流程
    private DeepSearchFlow deepSearchFlow ;

    // 所属机构ID
    private Long orgId ;

    // 所属部门ID
    private Long departmentId ;

    // 所属操作员ID
    private Long operatorId ;

    // 结果类型(summary/react)
    private String resultType ;

    // 查询的文本
    private String queryText ;

    // 知识库索引集合名称
    private String collectionIndexName ;

    // 知识库索引集合名称标识
    private String collectionIndexLabel ;

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
       return this.getSceneId() > 0 ;
    }
}
