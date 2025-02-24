package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

/**
     * 猜测用户问题数据内部类
     */
    @Data
    public class GuessWhatYouAskData {
        // 是否启用猜测用户问题功能
        private boolean isEnable;
        // 语音模型名称，可能用于语音提示猜测问题
        private String voiceModel;
        // 提示内容，用于引导猜测用户问题的规则
        private String prompt;
        // 语音速度，用于猜测问题语音提示的速度
        private int voiceSpeed;
    }