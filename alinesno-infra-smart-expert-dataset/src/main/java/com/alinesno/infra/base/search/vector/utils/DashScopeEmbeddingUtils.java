package com.alinesno.infra.base.search.vector.utils;

import com.alibaba.dashscope.embeddings.BatchTextEmbedding;
import com.alibaba.dashscope.embeddings.BatchTextEmbeddingParam;
import com.alibaba.dashscope.embeddings.BatchTextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.adapter.EmbeddingConsumer;
import com.alinesno.infra.smart.assistant.adapter.dto.TextEmbeddingRequest;
import com.alinesno.infra.smart.assistant.adapter.dto.TextEmbeddingResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 使用阿里服务完成文本转换 EmbeddingService类用于处理文本嵌入相关操作
 * <a href="https://help.aliyun.com/zh/dashscope/developer-reference/text-embedding-quick-start">参考文档</a>
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@NoArgsConstructor
@Setter
@Getter
@Component
public class DashScopeEmbeddingUtils {

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String apiKey;

    @Value("${alinesno.infra.gateway.embedding-model:text-embedding-v4}")
    private String embeddingModel ;

    @Autowired
    private EmbeddingConsumer embeddingConsumer;

    /**
     * 获取文本的嵌入向量
     * @param queryText
     * @return
     * @throws NoApiKeyException
     */
    public List<Double> getEmbeddingDoubles(String queryText) throws NoApiKeyException {
        // 将获取到的queryText转换成embedding内容
        List<TextEmbeddingResultItem> embeddingResultItems = embedSingleList(List.of(queryText)) ; //   embedding.getOutput().getEmbeddings() ;

        TextEmbeddingResultItem embeddingResultItem = embeddingResultItems.get(0) ;
        return embeddingResultItem.getEmbedding();
    }

    /**
     * 获取文本的嵌入向量
     * @param queryText
     * @return
     */
    public List<TextEmbeddingResultItem> embedSingleList(List<String> queryText) {
        TextEmbeddingRequest request = new TextEmbeddingRequest() ;

        request.setDimension("1024");
        request.setModel(embeddingModel);
        request.setEncoding_format("float");
        request.setInput(queryText);

        TextEmbeddingResponse response = embeddingConsumer.embeddings(request , apiKey) ;
        return response.getData() ;
    }

    /**
     * 处理批量文本嵌入
     *
     * @return
     * @throws ApiException      当API调用异常时抛出
     * @throws NoApiKeyException 当没有API密钥时抛出
     */
    public BatchTextEmbeddingResult embedBatch(String url) throws ApiException, NoApiKeyException {
        // 构建批量文本嵌入参数
        BatchTextEmbeddingParam param = BatchTextEmbeddingParam.builder()
                .model(BatchTextEmbedding.Models.TEXT_EMBEDDING_ASYNC_V1) // 设置模型
                .apiKey(apiKey) // 从环境变量中获取API密钥
                .url(url) // 设置文本文件的URL
                .build();
        // 初始化批量文本嵌入对象并调用嵌入方法
        BatchTextEmbedding textEmbedding = new BatchTextEmbedding();

        return textEmbedding.call(param);
    }

    public void parseAndProcess(String jsonString) throws IOException {
        // 解析JSON
        Map<?, ?> result = JSON.parseObject(jsonString, Map.class);
        String url = (String) ((Map<?, ?>)result.get("output")).get("url");

        // 下载文件
        byte[] fileContent = downloadFile(url);

        // 解压并读取内容
        String decompressedContent = decompressGz(fileContent);
        System.out.println(decompressedContent);
    }

    private byte[] downloadFile(String urlString) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(urlString);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toByteArray(entity) : new byte[0];
        }
    }

    private String decompressGz(byte[] compressedContent) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressedContent));
             BufferedReader reader = new BufferedReader(new InputStreamReader(gis))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * 从指定的URL读取文本内容。
     *
     * @param urlString URL地址
     * @return 文本内容
     * @throws Exception 如果发生错误
     */
    public static String readTextFromUrl(String urlString) throws Exception {
        StringBuilder content = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // 设置请求方法
            connection.setRequestMethod("GET");
            // 可选：设置超时时间
            connection.setConnectTimeout(5000); // 毫秒

            // 获取输入流并读取内容
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine).append("\n");
                }
            }
        } finally {
            // 关闭连接
            connection.disconnect();
        }

        return content.toString();
    }

}
