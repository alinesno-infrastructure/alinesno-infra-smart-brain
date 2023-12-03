package com.alinesno.infra.smart.brain.client.consumer;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.api.dto.ChatRequestDto;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Headers;
import com.dtflys.forest.annotation.Post;

/**
 * 请求会话
 */
@BaseRequest(baseURL = "http://localhost:30302")
@Headers({
        "Content-Type: application/json"
})
public interface LLMChatClient {

    /**
     * 获取流式内容响应接口
     * @param request
     */
    @Post("/api/llm/chatProcess")
    AjaxResult chatProcess(@Body ChatRequestDto request);

    /**
     * 获取实时内容响应接口
     * @param request
     */
    @Post("/api/llm/chatCompletions")
    AjaxResult chatCompletions(@Body ChatRequestDto request);

    /**
     * 获取离线内容生成接口
     * @param request
     */
    @Post("/api/llm/chatTask")
    AjaxResult sendChatTask(@Body BrainTaskDto request);

    /**
     * 根据业务ID获取内容
     * @param businessId
     */
    @Post("/api/llm/chatContent")
    AjaxResult postChatContent(String businessId);

}
