package com.alinesno.infra.smart.brain.service;

import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import okhttp3.Response;
import okhttp3.sse.EventSourceListener;

/**
 * 聊天GPT API服务接口
 *
 * @version 1.0.0
 * @since 2023年3月25日 上午6:23:43
 * @author luoxiaodong
 */
public interface ILLMApiService {

    /**
     * 获取聊天内容
     * @param completion
     * @return
     */
    Response chatCompletions(ChatCompletion completion);

    /**
     * 流式回答
     * @param chatCompletion
     * @param eventSourceListener
     */
    void streamChatCompletion(ChatCompletion chatCompletion, EventSourceListener eventSourceListener);

}