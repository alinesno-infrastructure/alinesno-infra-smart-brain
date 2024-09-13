package com.alinesno.infra.smart.brain.inference.llm;

import cn.hutool.http.ContentType;
import com.alinesno.infra.smart.brain.service.ILLMApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LLMApiServiceImpl implements ILLMApiService {

    private static OkHttpClient okHttpClient;

    @Value("${alinesno.infra.smart.brain.openapi.key:}")
    private String apiKey;

    /**
     * 自定义api host使用builder的方式构造client
     */
    @Value("${alinesno.infra.smart.brain.openapi.host:}")
    private String apiHost ;

    static {
        okHttpClient();
    }

    /**
     * 创建 OkHttpClient，自定义超时时间和拦截器
     */
    private static void okHttpClient() {

        long connectTimeout = 30;
        long writeTimeout = 30;
        long readTimeout = 30;

        if(okHttpClient == null){
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(connectTimeout, TimeUnit.SECONDS);
            client.writeTimeout(writeTimeout, TimeUnit.SECONDS);
            client.readTimeout(readTimeout, TimeUnit.SECONDS);

            okHttpClient = client.build();
        }

    }

    /**
     * 同步回答
     *
     * @param completion
     * @return
     */
    @Override
    @SuppressWarnings({ "deprecation", "unused" })
    public Response chatCompletions(ChatCompletion completion) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            completion.setModel(ChatCompletion.Model.GPT_3_5_TURBO.getName());
            String requestBody = mapper.writeValueAsString(completion);

            Request request = new Request.Builder()
                    .url(this.apiHost + "/v1/chat/completions")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .header("Authorization", "Bearer " + this.apiKey)
                    .build();

            return okHttpClient.newCall(request).execute() ;

        } catch (Exception e) {
            log.error("请求参数解析异常：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 流式输出，最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     *
     * @param chatCompletion      问答参数
     * @param eventSourceListener sse监听器
     */
    @Override
    @SuppressWarnings({ "deprecation", "unused" })
    public void streamChatCompletion(ChatCompletion chatCompletion, EventSourceListener eventSourceListener) {

        log.debug("open host = {} , key = {}" , apiHost , apiKey);

        if (!chatCompletion.isStream()) {
            chatCompletion.setStream(true);
        }

        chatCompletion.setModel(ChatCompletion.Model.GPT_3_5_TURBO.getName());
        try {
            EventSource.Factory factory = EventSources.createFactory(okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(chatCompletion);

            log.debug("requestBody = {}" , requestBody);

            Request request = new Request.Builder()
                    .url(apiHost + "/v1/chat/completions")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .header("Authorization", "Bearer " + apiKey)
                    .build();

            //创建事件
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);

        } catch (Exception e) {
            log.error("请求参数解析异常：{}", e.getMessage());
        }
    }

}
