package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.smart.assistant.api.quote.CitedAnswer;
import com.alinesno.infra.smart.assistant.api.quote.Document;

import java.util.List;

/**
 * 引用生成器统一接口
 * 定义生成带引用答案的标准方法
 */
public interface CitationGenerator {
    /**
     * 根据问题和文档生成带引用的答案
     * @param question 用户问题
     * @param documents 检索到的文档列表
     * @return 带引用的答案
     */
    CitedAnswer generateAnswerWithCitations(String question, List<Document> documents);
}