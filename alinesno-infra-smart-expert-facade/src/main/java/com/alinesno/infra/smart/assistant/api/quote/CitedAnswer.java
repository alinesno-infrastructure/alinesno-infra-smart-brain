package com.alinesno.infra.smart.assistant.api.quote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 带引用的回答类
 * - answer: 生成的答案文本
 * - citations: 引用列表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitedAnswer {
    private String answer;
    private List<Citation> citations;
}