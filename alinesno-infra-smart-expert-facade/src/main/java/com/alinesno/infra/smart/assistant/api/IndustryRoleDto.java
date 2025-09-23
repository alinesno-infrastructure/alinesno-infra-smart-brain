package com.alinesno.infra.smart.assistant.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alinesno.infra.smart.assistant.api.config.*;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * 角色数据传输对象
 */
@Data
public class IndustryRoleDto {

    private Long id;
    private Long industryCatalog; // 所属行业
    private String responsibilities; // 角色职责描述
    private String skills; // 所需技能描述
    private String roleLevel; // 角色等级
    private String roleType; //  角色类型，用于区分不同的角色类别
    private ModelConfig modelConfig; // 模型配置相关信息
    private List<String> knowledgeBaseIds; // 知识库ID列表，用于关联相关知识数据
    private List<String> selectionToolsData; // 选择工具数据列表，可能用于特定操作或查询的工具标识
    private boolean longTermMemoryEnabled; // 是否启用长期记忆功能
    private boolean voiceInputStatus; // 是否启用语音输入状态
    private boolean guessWhatYouAskStatus; // 是否用户问题建议
    private boolean voicePlayStatus; // 语音播放
    private boolean uploadStatus ; // 文件上传
    private boolean outputFileFormatStatus ; // 输出文件格式
    private boolean welcomeConfigStatus;  // 欢迎配置

    private boolean contextEngineeringEnable ; // 是否开启上下文工程
    private ContextEngineeringData contextEngineeringData ; // 上下文工程配置数据项

    private UploadData uploadData; // 文件上传配置
    private VoiceInputData voiceInputData; // 语音输入相关数据配置
    private VoicePlayData voicePlayData; // 语音播放
    private GuessWhatYouAskData guessWhatYouAskData; // 猜测用户问题相关数据配置
    private DatasetSearchConfig datasetSearchConfig ;   // 数据集搜索配置
    private OutputFileFormatData outputFileFormatData; // 输出文件格式
    private WelcomeConfigData welcomeConfigData ; // 欢迎配置
    private String roleAvatar; // 角色头像
    private String roleName; // 角色名称
    private String backstory; // 角色背景
    private String chainId; // 其他角色相关字段
    private String greeting; // 开场白
    private List<String> greetingQuestion; // 开场白问题
    private boolean askHumanHelp = false; // 是否需要人类帮助
    private String scriptType; // 执行类型(脚本script|流程flow|默认default)
    private String otherAttributes; // 其他角色相关字段
    private String promptContent; // 指令内容
    private Long chatCount = 0L; // 会话次数
    private boolean hasRecommended; // 推荐角色(用于Hero推荐)
    private int hasSale = 0; // 是否销售(1销售|0不可销售|9不可转售)
    private Long saleFromRoleId = 0L; // 销售来源角色ID
    private String knowledgeId; // 知识库ID
    private Long modelId; // 模型
    private String executeScript; // 执行脚本
    private String auditScript; // 审核脚本
    private String functionCallbackScript; // 功能回调脚本

    private Date addTime ;   // 添加时间
    private Date updateTime ;  // 更新时间

    // 假设存在一个静态方法 fromEntity，用于从 IndustryRoleEntity 转换为 IndustryRoleDto
    public static IndustryRoleDto fromEntity(IndustryRoleEntity entity) {
        IndustryRoleDto dto = new IndustryRoleDto();
        BeanUtils.copyProperties(entity, dto);

        // 处理特殊类型字段的转换
        if (entity.getModelConfig() != null) {
            dto.setModelConfig(JSONObject.parseObject(entity.getModelConfig(), ModelConfig.class));
        }

        if (entity.getKnowledgeBaseIds() != null) {
            List<String> knowledgeBaseIds = JSONObject.parseObject(entity.getKnowledgeBaseIds(), new TypeReference<>() {});
            dto.setKnowledgeBaseIds(knowledgeBaseIds);
        }

        if (entity.getSelectionToolsData() != null) {
            List<String> knowledgeBaseIds = JSONObject.parseObject(entity.getSelectionToolsData(), new TypeReference<>() {});
            dto.setSelectionToolsData(knowledgeBaseIds);
        }

        // 数据集配置
        if (entity.getDatasetSearchConfig() != null) {
            dto.setDatasetSearchConfig(JSONObject.parseObject(entity.getDatasetSearchConfig(), DatasetSearchConfig.class));
        }

        if (entity.getVoiceInputData() != null) {
            dto.setVoiceInputData(JSONObject.parseObject(entity.getVoiceInputData(), VoiceInputData.class));
        }

        if (entity.getGuessWhatYouAskData() != null) {
            dto.setGuessWhatYouAskData(JSONObject.parseObject(entity.getGuessWhatYouAskData(), GuessWhatYouAskData.class));
        }

        if (entity.getVoicePlayData() != null) {
            dto.setVoicePlayData(JSONObject.parseObject(entity.getVoicePlayData(), VoicePlayData.class));
        }

        // 文件上传
        if (entity.getUploadData() != null) {
            dto.setUploadData(JSONObject.parseObject(entity.getUploadData(), UploadData.class));
        }

        // 开场白问题
        if (entity.getGreetingQuestion() != null) {
            List<String> knowledgeBaseIds = JSONObject.parseObject(entity.getGreetingQuestion(), new TypeReference<>() {});
            dto.setGreetingQuestion(knowledgeBaseIds);
        }

        // 输出文件格式
        if (entity.getOutputFileFormatData() != null) {
            dto.setOutputFileFormatData(JSONObject.parseObject(entity.getOutputFileFormatData(), OutputFileFormatData.class));
        }

        // 欢迎配置
        if (entity.getWelcomeConfigData() != null) {
            dto.setWelcomeConfigData(JSONObject.parseObject(entity.getWelcomeConfigData(), WelcomeConfigData.class));
        }

        // 上下文工程配置
        if (entity.getContextEngineeringData() != null) {
            dto.setContextEngineeringData(JSONObject.parseObject(entity.getContextEngineeringData(), ContextEngineeringData.class));
        }

        return dto;
    }
}