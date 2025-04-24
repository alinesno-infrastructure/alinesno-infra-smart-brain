package com.alinesno.infra.smart.assistant.scene.common.utils;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修订类，用于表示文本修订建议
 */
@NoArgsConstructor
@Data
public class Revision {

    // 修改的原因
    private String reason;
    // 原始文本
    String originalText;
    // 建议的文本列表
    String suggestedTexts;
    // 修订的作者
    String author;

    /**
     * 构造函数，用于创建修订对象
     *
     * @param originalText 原始文本
     * @param suggestedTexts 建议的文本列表
     */
    public Revision(String originalText, String suggestedTexts, String reason) {
        this.originalText = originalText;
        this.suggestedTexts = suggestedTexts;
        this.reason = reason;
    }

}
