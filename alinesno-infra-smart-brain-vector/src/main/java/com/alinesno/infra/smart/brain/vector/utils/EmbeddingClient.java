package com.alinesno.infra.smart.brain.vector.utils;

import com.alinesno.infra.smart.brain.vector.config.MilvusConfiguration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.List;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class EmbeddingClient {

    @Autowired
    private MilvusConfiguration configuration ;

    private final static OkHttpClient client = new OkHttpClient();

    public List<List<Float>> doEmbedding(List<String> msg) {
       return null ;
    }

    public List<Float> doEmbedding(String msg) {
        List<Float> floats = new ArrayList<>();
        try {
            // 创建请求体
            RequestBody body = new FormBody.Builder()
                    .add("text", msg)
                    .build();

            // 创建请求
            Request request = new Request.Builder()
                    .url(configuration.getEmbeddingAddr())
                    .post(body)
                    .build();

            // 发起请求
            Response response = client.newCall(request).execute();

            // 处理响应
            if (response.isSuccessful()) {

                assert response.body() != null;

                String responseBody = response.body().string();
                String[] numbers = responseBody.replace("[", "").replace("]", "").split(",");
                for (String num : numbers) {
                    floats.add(Float.parseFloat(num));
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return floats;
    }
}