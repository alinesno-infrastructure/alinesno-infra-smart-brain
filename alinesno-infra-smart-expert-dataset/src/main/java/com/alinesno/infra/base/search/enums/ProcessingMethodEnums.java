package com.alinesno.infra.base.search.enums;

import lombok.Getter;

// 处理方式枚举类
@Getter
public enum ProcessingMethodEnums {

    DIRECT_SEGMENTATION("direct_segmentation", "直接分段"),
    ENHANCED_PROCESSING("enhanced_processing", "增强处理"),
    QUESTION_ANSWER_SPLIT("question_answer_split", "问答拆分");

    private final String code;
    private final String description;

    ProcessingMethodEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }
}