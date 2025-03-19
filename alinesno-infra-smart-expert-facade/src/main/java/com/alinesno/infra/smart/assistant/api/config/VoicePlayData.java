package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

/**
     * 语音播放
     */
    @Data
    public class VoicePlayData {
        // 语音模型名称，可能用于语音播放
        private String voiceModel;
        // 播放音色
        private String voiceSpeechModel ;
        // 语音速度，用于语音播放的速度
        private int speechRate;
    }