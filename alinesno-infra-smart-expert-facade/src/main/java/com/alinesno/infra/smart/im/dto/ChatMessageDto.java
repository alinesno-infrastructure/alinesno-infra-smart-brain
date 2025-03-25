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
    private Long messageId ; // 消息ID(设置成Long前端可为null)

    private String roleType;
    private String icon ;
    private String name;
    private String dateTime ;
    private Object chatText;
    private String reasoningText ;  // 推理内容

    private List<String> greetingQuestion;  // 用户推荐问题列表

    private List<FileAttachmentDto> fileAttributeList = new ArrayList<>();  // 文件列表(这条消息中的文件列表)

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
    private List<MessageReferenceDto> contentReferenceArticle ; // 消息引用文章

    private String errorMessage; // 错误信息
    private boolean hasError ; // 是否异常

    private boolean hasExecuteTool ; // 是否有执行能力(此为脚本配置角色特有，其它类型无)
    private MessageUsageDto usage ;   // 角色消耗能力，包括消耗的token数，以及消耗的时长

    private boolean voicePlayStatus = false ; // 语音播放能力(默认为false)

}
