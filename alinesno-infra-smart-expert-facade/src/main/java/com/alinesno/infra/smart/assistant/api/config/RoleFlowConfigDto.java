package com.alinesno.infra.smart.assistant.api.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用于表示角色反应配置的数据传输对象（DTO）
 */
@Data
public class RoleFlowConfigDto {

    @Min(value = 1, message = "角色ID不能为空")
    private long roleId ;

    // 问候语，查询专员与用户交互时的起始问候
    @NotBlank(message = "问候语不能为空")
    private String greeting;

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

}