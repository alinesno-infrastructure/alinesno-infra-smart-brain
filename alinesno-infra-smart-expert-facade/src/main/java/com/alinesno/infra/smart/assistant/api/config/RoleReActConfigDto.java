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

    // 语音输入相关数据配置
    private VoiceInputData voiceInputData;

    // 语音播放
    private VoicePlayData voicePlayData ;

    // 猜测用户问题相关数据配置
    private GuessWhatYouAskData guessWhatYouAskData;

    /**
     * 模型配置内部类
     */
    @Data
    public static class ModelConfig {
        // 人工智能模型名称
        private String aiModel;
        // 记忆轮数，可能用于记录对话轮次相关信息
        private int memoryRounds;
        // 回复内容长度限制
        private int replyLimit;
        // 温度参数，影响生成文本的随机性，值越高越随机
        private double temperature;
        // 核采样参数，用于控制生成文本的多样性
        private double topP;
        // 回复格式，例如json格式
        private String replayFormat;
        // 停止序列，当生成文本遇到该序列时停止
        private String stopSequences;
        // 是否启用回复格式功能
        private boolean replyFormatEnabled ;
        // 是否启用停止序列功能
        private boolean stopSequencesEnabled ;
    }

    /**
     * 语音输入数据内部类
     */
    @Data
    public static class VoiceInputData {
        // 是否启用语音输入功能
        private boolean isEnable;
        // 语音模型名称
        private String voiceModel;
        // 语音语速
        private int speechRate;
    }

    /**
     * 语音播放
     */
    @Data
    public static class VoicePlayData {
        // 语音模型名称，可能用于语音播放
        private String voiceModel;
        // 语音速度，用于语音播放的速度
        private int speechRate;
    }


    /**
     * 猜测用户问题数据内部类
     */
    @Data
    public static class GuessWhatYouAskData {
        // 是否启用猜测用户问题功能
        private boolean isEnable;
        // 语音模型名称，可能用于语音提示猜测问题
        private String voiceModel;
        // 提示内容，用于引导猜测用户问题的规则
        private String prompt;
        // 语音速度，用于猜测问题语音提示的速度
        private int voiceSpeed;
    }
}