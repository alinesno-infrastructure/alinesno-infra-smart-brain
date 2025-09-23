package com.alinesno.infra.smart.assistant.enums;

import lombok.Getter;

/**
 * 策略枚举
 */
@Getter
public enum ContextEngineeringStrategyEnum {
    /**
     * 切割策略
     */
    SPLIT("split" , "切割"),
    /**
     * RAG知识库
     */
    RAG("rag" , "RAG知识库"),
    /**
     * 大模型总结策略
     */
    LLM_SUMMARY("llm_summary" , "大模型总结") ;

    private final String value;
    private final String label;

    ContextEngineeringStrategyEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

}