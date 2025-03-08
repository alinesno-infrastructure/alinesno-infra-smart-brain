package com.alinesno.infra.smart.assistant.adapter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class SpeechRecognitionService {

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String apiKey ;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String submitTask(List<String> fileUrls) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://dashscope.aliyuncs.com/api/v1/services/audio/asr/transcription");
            post.setHeader("Authorization", "Bearer " + apiKey);
            post.setHeader("X-DashScope-Async", "enable");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (String fileUrl : fileUrls) {
                File file = new File(fileUrl);
                builder.addBinaryBody("file_urls", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            }
            builder.addTextBody("model", "paraformer-v2");
            builder.addTextBody("parameters[channel_id]", "0");
            builder.addTextBody("parameters[language_hints]", "zh,en");
            builder.addTextBody("parameters[vocabulary_id]", "vocab-Xxxx"); // 根据实际需要设置或留空

            HttpEntity multipart = builder.build();
            post.setEntity(multipart);

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (response.getStatusLine().getStatusCode() == 200) {
                    return extractFieldFromJson(responseString, "task_id");
                } else {
                    System.out.println("task failed!");
                    System.out.println(responseString);
                    return null;
                }
            }
        }
    }

    public String waitForComplete(String taskId) throws IOException, InterruptedException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://dashscope.aliyuncs.com/api/v1/tasks/" + taskId);
            post.setHeader("Authorization", "Bearer " + apiKey);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("X-DashScope-Async", "enable");

            boolean pending = true;
            while (pending) {
                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String status = extractFieldFromJson(responseString, "task_status");
                        if ("SUCCEEDED".equals(status)) {
                            System.out.println("task succeeded!");
                            pending = false;
                            return extractFieldFromJson(responseString, "results");
                        } else if ("RUNNING".equals(status) || "PENDING".equals(status)) {
                            Thread.sleep(500); // 简单等待机制
                        } else {
                            System.out.println("task failed!");
                            pending = false;
                        }
                    } else {
                        System.out.println("query failed!");
                        pending = false;
                    }
                }
            }
        }
        return null;
    }

    private  String extractFieldFromJson(String json, String fieldName) throws IOException {
        // 使用Jackson从JSON字符串中提取指定字段的值
        return objectMapper.readTree(json).path(fieldName).asText();
    }
}