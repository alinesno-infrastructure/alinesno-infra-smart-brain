package com.alinesno.infra.smart.im.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息实体信息
 */
@Data
public class ChatMessageDto {

    private long channelId ; // 所属频道Id
    private long roleId ; //所属角色Id
    private long accountId ; // 登陆用户ID

    private String roleType;
    private String icon ;
    private String name;
    private String dateTime ;
    private Object chatText;
    private String reasoningText ;  // 推理内容

    private FlowStepStatusDto flowStep ; // 流程节点运行状态
    private List<FlowStepStatusDto> flowStepArr = new ArrayList<>(); // 流程信息
    private String readerType ; // 读取方式和类型

    private boolean isLoading = false ; // 是否还在加载中
    private String status ;  // 运行状态

    @JsonSerialize(using = ToStringSerializer.class)
    private long businessId ; // 生成的唯一业务ID标识
    private String className ; // 返回的样式标识

    // fix:用于消息类型的区分(同一businessId的情况下)
    private boolean llmStream = false ; // 是否为LLM输出流
    private List<MessageReferenceDto> contentReferenceArticle ; // 消息引用

}
