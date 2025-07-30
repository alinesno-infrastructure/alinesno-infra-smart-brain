package com.alinesno.infra.smart.assistant.role.quote;

import com.alinesno.infra.smart.assistant.api.quote.Citation;
import com.alinesno.infra.smart.assistant.api.quote.CitedAnswer;
import com.alinesno.infra.smart.assistant.api.quote.Document;
import com.alinesno.infra.smart.assistant.service.CitationGenerator;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 方法3实现：通过提示词让模型直接生成带引用的回答
 * 特点：使用XML格式让模型返回结构化结果，然后解析
 */
@Slf4j
public class PromptBasedCitationGenerator implements CitationGenerator {
    
    @Override
    public CitedAnswer generateAnswerWithCitations(String question, List<Document> documents) {
        log.debug("通过XML提示词为问题[{}]生成带引用的答案", question);
        
        // 1. 获取模型生成的XML格式响应
        String xmlResponse = generateXmlFormattedAnswer(question, documents);
        
        // 2. 解析XML响应
        return parseXmlResponse(xmlResponse);
    }
    
    /**
     * 模拟LLM生成XML格式回答
     */
    private String generateXmlFormattedAnswer(String question, List<Document> documents) {
        // 实际实现中会调用LLM接口并传入特定提示词
        return """
            <cited_answer>
                <answer>猎豹的奔跑速度可达每小时93至104公里。</answer>
                <citations>
                    <citation><source_id>1</source_id><quote>猎豹能够以每小时93至104公里的速度奔跑...</quote></citation>
                </citations>
            </cited_answer>
            """;
    }
    
    /**
     * 解析XML格式的回答
     * @param xml XML格式字符串
     * @return 解析后的带引用答案
     */
    private CitedAnswer parseXmlResponse(String xml) {
        // 使用正则表达式解析XML（实际项目建议使用XML解析库）
        Pattern answerPattern = Pattern.compile("<answer>(.*?)</answer>");
        Pattern citationPattern = Pattern.compile("<citation><source_id>(\\d+)</source_id><quote>(.*?)</quote></citation>");
        
        // 提取答案文本
        Matcher answerMatcher = answerPattern.matcher(xml);
        String answer = answerMatcher.find() ? answerMatcher.group(1) : "";
        
        // 提取引用列表
        List<Citation> citations = new ArrayList<>();
        Matcher citationMatcher = citationPattern.matcher(xml);
        while (citationMatcher.find()) {
            citations.add(new Citation(
                Integer.parseInt(citationMatcher.group(1)), // sourceId
                citationMatcher.group(2)                  // quote
            ));
        }
        
        return new CitedAnswer(answer, citations);
    }
}