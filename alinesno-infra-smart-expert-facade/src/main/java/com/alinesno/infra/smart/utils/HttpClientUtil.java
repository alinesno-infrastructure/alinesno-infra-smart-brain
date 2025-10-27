package com.alinesno.infra.smart.utils;

import com.agentsflex.core.llm.client.HttpClient;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * HttpClient工具类
 */
public class HttpClientUtil {

    /**
     * 创建OkHttpClient，默认超时为5分钟
     * @return
     */
    public static HttpClient buildContextClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES);

        OkHttpClient client = builder.build();

        return new HttpClient(client);
    }

}
