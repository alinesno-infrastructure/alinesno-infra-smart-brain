package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

/**
     * 模型配置内部类
     */
    @Data
    public class ModelConfig {
        // 是否启用
        private boolean enabled;
        // 记忆轮数，可能用于记录对话轮次相关信息
        private int memoryRounds;
        // 回复内容长度限制
        private int replyLimit;
        // 温度参数，影响生成文本的随机性，值越高越随机
        private String temperature;
        // 核采样参数，用于控制生成文本的多样性
        private String topP;
        // 最多循环次数
        private int maxLoop = 6;
        // 失败则跳出
        private boolean failBreak;
    }




