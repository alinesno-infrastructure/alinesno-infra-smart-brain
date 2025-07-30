package com.alinesno.infra.smart.assistant.role.quote;

import com.alinesno.infra.smart.assistant.api.quote.Citation;
import com.alinesno.infra.smart.assistant.api.quote.CitedAnswer;
import com.alinesno.infra.smart.assistant.api.quote.Document;
import com.alinesno.infra.smart.assistant.service.CitationGenerator;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * 方法5实现：生成后处理的引用生成器
 * 特点：先生成答案，再对答案进行引用标注
 */
@Slf4j
public class PostGenerationCitationGenerator implements CitationGenerator {
    
    public CitedAnswer generateAnswerWithCitations(String question, List<Document> documents) {
        log.debug("为问题[{}]采用生成后处理方式添加引用", question);
        
        // 1. 首先生成初始答案（不带引用）
        String initialAnswer = generateInitialAnswer(question, documents);
        
        // 2. 然后对答案进行引用标注
        List<Citation> citations = annotateCitations(initialAnswer, documents);
        
        return new CitedAnswer(initialAnswer, citations);
    }
    
    /**
     * 生成初始答案（不带引用）
     */
    private String generateInitialAnswer(String question, List<Document> documents) {
        // 模拟LLM生成答案
        return "猎豹的奔跑速度可达每小时93至104公里。";
    }
    
    /**
     * 对已有答案进行引用标注
     * @param answer 已生成的答案
     * @param documents 参考文档
     * @return 引用列表
     */
    private List<Citation> annotateCitations(String answer, List<Document> documents) {
        List<Citation> citations = new ArrayList<>();
        
        // 简化实现：如果答案包含速度信息且文档不为空，则添加引用
        if (answer.contains("93至104公里") && !documents.isEmpty()) {
            citations.add(new Citation(
                documents.get(0).getId(), 
                "猎豹能够以每小时93至104公里的速度奔跑..."
            ));
        }
        
        return citations;
    }
}