package com.alinesno.infra.smart.assistant.role.quote;

import com.alinesno.infra.smart.assistant.api.quote.CitedAnswer;
import com.alinesno.infra.smart.assistant.api.quote.Document;
import com.alinesno.infra.smart.assistant.service.CitationGenerator;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * 方法4实现：检索后处理的引用生成器
 * 特点：先对检索结果进行优化处理，再生成引用
 */
@Slf4j
public class PostRetrievalCitationGenerator implements CitationGenerator {
    
    // 委托给方法2的生成器
    private final CitationGenerator delegate = new DocumentIdAndQuoteCitationGenerator();
    
    @Override
    public CitedAnswer generateAnswerWithCitations(String question, List<Document> documents) {
        log.debug("对问题[{}]的检索结果进行后处理", question);
        
        // 1. 对检索到的文档进行预处理（过滤、压缩等）
        List<Document> processedDocs = processRetrievedDocuments(question, documents);
        
        // 2. 使用标准方法生成引用
        return delegate.generateAnswerWithCitations(question, processedDocs);
    }
    
    /**
     * 对检索结果进行预处理
     * @return 处理后的文档列表
     */
    private List<Document> processRetrievedDocuments(String question, List<Document> documents) {
        // 简化实现：只取前3个文档
        // 实际实现可能包括：分块、过滤、重新排序等
        return documents.subList(0, Math.min(3, documents.size()));
    }
}