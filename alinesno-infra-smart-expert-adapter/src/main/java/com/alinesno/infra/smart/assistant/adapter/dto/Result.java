package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
     * 重新排序的结果实体类
     * 包含文档、索引和相关性分数
     */
    @Data
    public class Result {
        // 文档信息
        private Document document;

        // 文档在列表中的索引位置
        private int index;

        // 文档的相关性分数，用于表示文档的相关程度
        private double relevance_score;
    }