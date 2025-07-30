package com.alinesno.infra.smart.assistant.api.quote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 引用信息类
 * - sourceId: 引用的文档ID
 * - quote: 引用的文本片段
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Citation {
    private int sourceId;
    private String quote;
}