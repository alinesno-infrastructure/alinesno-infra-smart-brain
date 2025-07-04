package com.alinesno.infra.smart.assistant.adapter;


import com.alinesno.infra.smart.assistant.adapter.dto.TextEmbeddingRequest;
import com.alinesno.infra.smart.assistant.adapter.dto.TextEmbeddingResponse;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;

/**
 * 文本嵌入接口
 */
@BaseRequest(
//        baseURL = "https://dashscope.aliyuncs.com/compatible-mode/v1/",
        baseURL = "#{alinesno.infra.gateway.embedding}" ,
        connectTimeout = 30000,
        readTimeout = 60000)
public interface EmbeddingConsumer {

    /**
     * 发送文本排序请求
     *
     * @param request 请求对象
     * @return 响应字符串
     */
    @Post(url = "/embeddings" , contentType = "application/json")
    TextEmbeddingResponse embeddings(@Body TextEmbeddingRequest request , @Header("Authorization") String authorization);

}