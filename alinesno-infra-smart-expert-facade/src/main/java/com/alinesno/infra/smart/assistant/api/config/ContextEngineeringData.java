package com.alinesno.infra.smart.assistant.api.config;

import com.alinesno.infra.smart.assistant.enums.ContextEngineeringStrategyEnum;
import lombok.Data;

/**
 * 上下文工程数据
 */
@Data
public class ContextEngineeringData {

    /**
     * 是否启用
     */
    private boolean isEnable = true;

    /**
     * 策略（切割/大模型总结)
     */
    private String strategy = ContextEngineeringStrategyEnum.SPLIT.getValue();

    /**
     * LLM模型
     */
    private String llmModel;

    /**
     * 概要提示
     */
    private String summaryPrompt;

    /**
     * 上下文长度限制
     */
    private int maxContextLength = 4096;

    /**
     * 上下文缓存过期时间（分钟）
     */
    private int cacheExpireMinutes = 60;
}
