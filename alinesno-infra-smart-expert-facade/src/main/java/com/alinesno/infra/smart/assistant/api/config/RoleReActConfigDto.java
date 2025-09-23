package com.alinesno.infra.smart.assistant.api.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 用于表示角色反应配置的数据传输对象（DTO）
 */
@Data
public class RoleReActConfigDto {

    @Min(value = 1, message = "角色ID不能为空")
    private long roleId ;

    // 模型
    @Min(value = 1, message = "模型ID不能为空")
    private long modelId;

    // 提示内容，用于引导用户提问
    @NotBlank(message = "提示内容不能为空")
    private String promptContent;

    // 问候语，查询专员与用户交互时的起始问候
    @NotBlank(message = "问候语不能为空")
    private String greeting;

    // 问候语问题
    private List<String> greetingQuestion ;

    // 模型配置相关信息
    private ModelConfig modelConfig;

    // 知识库ID列表，用于关联相关知识数据
    private List<String> knowledgeBaseIds;

    // 选择工具数据列表，可能用于特定操作或查询的工具标识
    private List<String> selectionToolsData;

    // 是否启用长期记忆功能
    private boolean longTermMemoryEnabled;

    // 是否启用语音输入状态
    private boolean voiceInputStatus;

    // 是否用户问题建议
    private boolean guessWhatYouAskStatus ;

    // 语音播放
    private boolean voicePlayStatus ;

    // 文件上传
    private boolean uploadStatus ;

    // 文件格式输出
    private boolean outputFileFormatStatus ;

    // 上下文工程
    private boolean contextEngineeringEnable ;

    // 文件上传相关数据
    private UploadData uploadData ;

    // 语音输入相关数据配置
    private VoiceInputData voiceInputData;

    // 语音播放
    private VoicePlayData voicePlayData ;

    // 猜测用户问题相关数据配置
    private GuessWhatYouAskData guessWhatYouAskData;

    // 语料搜索配置
    private DatasetSearchConfig datasetSearchConfig ;

    // 文件格式输出
    private OutputFileFormatData outputFileFormatData ;

    // 上下文工程
    private ContextEngineeringData contextEngineeringData ;
}