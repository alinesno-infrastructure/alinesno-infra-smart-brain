package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
     * 文档实体类
     * 简单地封装了文本内容
     */
    @Data
    public class Document {
        // 文本内容，文档的实际文本信息
        private String text;
    }