package com.alinesno.infra.smart.assistant.role.quote;

import com.alinesno.infra.smart.assistant.api.quote.Citation;
import com.alinesno.infra.smart.assistant.api.quote.CitedAnswer;
import com.alinesno.infra.smart.assistant.api.quote.Document;
import com.alinesno.infra.smart.assistant.service.CitationGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法2实现：标注文档ID和文本片段的引用生成器
 * 特点：不仅记录文档ID，还包含答案引用的具体文本片段
 */
@Slf4j
public class DocumentIdAndQuoteCitationGenerator implements CitationGenerator {
    
    @Override
    public CitedAnswer generateAnswerWithCitations(String question, List<Document> documents) {
        log.debug("开始为问题[{}]生成带文档ID和文本引用的答案", question);
        
        // 1. 生成基础答案
        String answer = generateAnswerBasedOnDocuments(question, documents);
        
        // 2. 构建引用列表（取相关性最高的2个文档）
        List<Citation> citations = new ArrayList<>();
        for (int i = 0; i < Math.min(2, documents.size()); i++) {
            Document doc = documents.get(i);
            // 从文档内容中提取相关片段作为引用
            citations.add(new Citation(doc.getId(), extractRelevantQuote(doc.getContent())));
        }
        
        return new CitedAnswer(answer, citations);
    }
    
    /**
     * 模拟答案生成过程
     */
    private String generateAnswerBasedOnDocuments(String question, List<Document> documents) {
        return "根据检索到的文档，答案是...";
    }
    
    /**
     * 从文档内容中提取相关引用片段
     * @param content 文档内容
     * @return 提取的引用片段
     */
    private String extractRelevantQuote(String content) {
        // 简化实现：取前50个字符
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
}