package com.alinesno.infra.smart.assistant.adapter;


import com.alinesno.infra.smart.assistant.adapter.dto.TextEmbeddingRequest;
import com.alinesno.infra.smart.assistant.adapter.dto.TextEmbeddingResponse;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;

/**
 * 文本嵌入接口
 */
@BaseRequest(
        baseURL = "#{alinesno.infra.gateway.host}/base-embedding" ,
        connectTimeout = 30000,
        readTimeout = 60000)
public interface EmbeddingConsumer {

    /**
     * 发送文本排序请求
     *
     * @param request 请求对象
     * @return 响应字符串
     */
    @Post(url = "/v1/embeddings" , contentType = "application/json")
    TextEmbeddingResponse embeddings(@Body TextEmbeddingRequest request);

}