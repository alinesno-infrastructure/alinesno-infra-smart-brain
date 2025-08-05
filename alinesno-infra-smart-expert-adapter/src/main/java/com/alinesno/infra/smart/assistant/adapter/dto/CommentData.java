package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批注数据封装类
 */
@NoArgsConstructor
@Data
public class CommentData {
    private String selectedText;
    private String commentText;
    private String author;

    public CommentData(String selectedText, String commentText, String author) {
        this.selectedText = selectedText;
        this.commentText = commentText;
        this.author = author;
    }

}