package com.alinesno.infra.smart.assistant.api.quote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档类
 * - id: 文档唯一标识
 * - title: 文档标题
 * - content: 文档内容
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    private int id;
    private String title;
    private String content;
}