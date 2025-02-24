package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

/**
     * 模型配置内部类
     */
    @Data
    public class ModelConfig {
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




