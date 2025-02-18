package com.alinesno.infra.smart.assistant.adapter;


import com.alinesno.infra.smart.assistant.adapter.dto.TextRerankRequest;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;

@BaseRequest(baseURL = "https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank",
        connectTimeout = 30000,
        readTimeout = 60000)
public interface RerankConsumer {

    /**
     * 发送文本排序请求
     *
     * @param request 请求对象
     * @return 响应字符串
     */
    @Post(url = "/text-rerank" , contentType = "application/json")
    String rerank(@Body TextRerankRequest request , @Header("Authorization") String authorization);

}