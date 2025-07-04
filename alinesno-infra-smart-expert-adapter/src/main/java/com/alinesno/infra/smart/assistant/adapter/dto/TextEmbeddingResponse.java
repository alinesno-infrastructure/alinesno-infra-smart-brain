package com.alinesno.infra.smart.assistant.adapter.dto;

import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import lombok.Data;

import java.util.List;

/**
 * 向量文本返回
 */
@Data
public class TextEmbeddingResponse {

    private List<TextEmbeddingResultItem> data ;
    private String model ;
    private String object ;
    private Usage usage ;
    private String id ;

    @Data
    public static class Usage {
        private int prompt_tokens ;
        private int total_tokens ;
    }

}
