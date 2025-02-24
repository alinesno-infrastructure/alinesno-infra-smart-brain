package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

/**
     * 语音输入数据内部类
     */
    @Data
    public class VoiceInputData {
        // 是否启用语音输入功能
        private boolean isEnable;
        // 语音模型名称
        private String voiceModel;
        // 语音语速
        private int speechRate;
    }
