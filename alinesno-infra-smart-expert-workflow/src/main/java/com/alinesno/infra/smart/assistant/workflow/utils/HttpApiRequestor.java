package com.alinesno.infra.smart.assistant.workflow.utils;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.HttpApiNodeData;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * 该类用于根据 HttpApiNodeData 对象发起 HTTP 请求
 */
public class HttpApiRequestor {

    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 根据 HttpApiNodeData 对象发起 HTTP 请求并返回响应内容
     *
     * @param nodeData 包含 HTTP 请求所需信息的对象
     * @return 响应内容，如果请求失败则返回错误信息
     */
    public static String sendRequest(HttpApiNodeData nodeData) {
        // 检查必要参数是否为空
        if (nodeData == null) {
            return "错误：传入的 HttpApiNodeData 对象为空";
        }
        if (nodeData.getUrl() == null || nodeData.getUrl().isEmpty()) {
            return "错误：请求 URL 不能为空";
        }
        if (nodeData.getMethod() == null || nodeData.getMethod().isEmpty()) {
            return "错误：请求方法不能为空";
        }
        if (nodeData.getBodyType() == null || nodeData.getBodyType().isEmpty()) {
            return "错误：请求体类型不能为空";
        }

        try {
            // 构建请求 URL
            HttpUrl.Builder urlBuilder = HttpUrl.parse(nodeData.getUrl()).newBuilder();
            List<HttpApiNodeData.Param> params = nodeData.getParams();
            if (params != null) {
                for (HttpApiNodeData.Param param : params) {
                    if (param.getKey() == null || param.getValue() == null) {
                        return "错误：请求参数的键或值不能为空";
                    }
                    urlBuilder.addQueryParameter(param.getKey(), param.getValue());
                }
            }
            HttpUrl url = urlBuilder.build();

            // 构建请求体
            MediaType mediaType = MediaType.parse(nodeData.getBodyType());
            RequestBody requestBody = RequestBody.create(mediaType, "".getBytes());

            // 构建请求
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .method(nodeData.getMethod(), requestBody);

            // 添加请求头
            List<HttpApiNodeData.Param> headers = nodeData.getHeaders();
            if (headers != null) {
                for (HttpApiNodeData.Param header : headers) {
                    if (header.getKey() == null || header.getValue() == null) {
                        return "错误：请求头的键或值不能为空";
                    }
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }

            Request request = requestBuilder.build();

            // 发起请求
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        return responseBody.string();
                    }
                }
                return "请求失败，响应码: " + response.code();
            }
        } catch (IOException e) {
            return "请求发生 I/O 异常: " + e.getMessage();
        } catch (Exception e) {
            return "请求发生未知异常: " + e.getMessage();
        }
    }
}