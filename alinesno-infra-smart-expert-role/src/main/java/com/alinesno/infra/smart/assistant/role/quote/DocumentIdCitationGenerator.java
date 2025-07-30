package com.alinesno.infra.smart.assistant.role.quote;

import com.alinesno.infra.smart.assistant.api.quote.Citation;
import com.alinesno.infra.smart.assistant.api.quote.CitedAnswer;
import com.alinesno.infra.smart.assistant.api.quote.Document;
import com.alinesno.infra.smart.assistant.service.CitationGenerator;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 方法1实现：仅标注文档ID的引用生成器
 * 特点：只记录答案引用的文档ID，不包含具体文本片段
 */
@Slf4j
public class DocumentIdCitationGenerator implements CitationGenerator {
    
    @Override
    public CitedAnswer generateAnswerWithCitations(String question, List<Document> documents) {
        log.debug("开始为问题[{}]生成带文档ID引用的答案", question);
        
        // 1. 基于文档生成答案文本
        String answer = generateAnswerBasedOnDocuments(question, documents);
        
        // 2. 提取引用的文档ID（这里简化为取前2个文档）
        List<Citation> citations = documents.stream()
            .limit(2)
            .map(doc -> new Citation(doc.getId(), "")) // 不包含具体引用文本
            .collect(Collectors.toList());
        
        return new CitedAnswer(answer, citations);
    }
    
    /**
     * 模拟LLM根据文档生成答案的过程
     */
    private String generateAnswerBasedOnDocuments(String question, List<Document> documents) {
        // 实际实现中会调用LLM接口
        return "根据检索到的文档，答案是...";
    }
}