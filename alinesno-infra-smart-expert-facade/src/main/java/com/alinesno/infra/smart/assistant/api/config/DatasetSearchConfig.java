package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

@Data
public class DatasetSearchConfig {
    // 搜索类型
    private String searchType;
    // 引用限制
    private int quoteLimit;
    // 是否重新排序结果
    private int reorderResults;
    // 最小相关性
    private double minRelevance;
}
