package com.alinesno.infra.smart.brain.inference.llm;

import com.alinesno.infra.smart.brain.client.OpenAiClient;
import com.alinesno.infra.smart.brain.service.ILLMApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LLMApiServiceImpl implements ILLMApiService {

    public static OpenAiClient client = null;

    @Value("${alinesno.infra.smart.brain.openapi.key:}")
    private String apiKey;

    /**
     * 自定义api host使用builder的方式构造client
     */
    @Value("${alinesno.infra.smart.brain.openapi.host}")
    private String apiHost ;

    @Override
    public OpenAiClient getClient() {

        if (client != null) {
            return client;
        }

        // 推荐这种构造方式
        client = OpenAiClient
                .builder()
                .connectTimeout(60)
                .readTimeout(60*2*1000)
                .writeTimeout(60*2*1000)
                .apiKey(apiKey)
                .apiHost(apiHost)
                .build();

        return client;
    }


}
