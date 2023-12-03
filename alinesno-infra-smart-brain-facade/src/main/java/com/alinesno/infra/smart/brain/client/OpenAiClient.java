package com.alinesno.infra.smart.brain.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Completion;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;


/**
 * 描述： open ai 客户端
 * @author luoxiaodong
 * @version 1.0.0
 */
@NoArgsConstructor
public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class) ;

    private String apiKey;

    /**
     * 自定义api host使用builder的方式构造client
     */
    private String apiHost ;

    private OkHttpClient okHttpClient;
    /**
     * 连接超时
     */
    private long connectTimeout;
    /**
     * 写超时
     */
    private long writeTimeout;
    /**
     * 读超时
     */
    private long readTimeout;
    /**
     * okhttp 代理
     */
    private Proxy proxy;


    /**
     * 创建OpenAiClient，自定义OkHttpClient
     *
     * @param apiKey         key
     * @param connectTimeout 连接超时时间 单位秒
     * @param writeTimeout   写超时 单位秒
     * @param readTimeout    超时 单位秒
     * @param proxy          代理
     */
    public OpenAiClient(String apiKey, long connectTimeout, long writeTimeout, long readTimeout, Proxy proxy) {
        this.apiKey = apiKey;
        this.connectTimeout = connectTimeout;
        this.writeTimeout = writeTimeout;
        this.readTimeout = readTimeout;
        this.proxy = proxy;
        this.okHttpClient(connectTimeout, writeTimeout, readTimeout, proxy);
    }

    /**
     * 创建OpenAiClient，自定义OkHttpClient
     *
     * @param apiKey         key
     * @param connectTimeout 连接超时时间 单位秒
     * @param writeTimeout   写超时 单位秒
     * @param readTimeout    超时 单位秒
     */
    public OpenAiClient(String apiKey, long connectTimeout, long writeTimeout, long readTimeout) {
        this.apiKey = apiKey;
        this.connectTimeout = connectTimeout;
        this.writeTimeout = writeTimeout;
        this.readTimeout = readTimeout;
        this.okHttpClient(connectTimeout, writeTimeout, readTimeout, null);
    }

    /**
     * 创建OpenAiClient，使用默认的超时时间
     * 注意当超时时间过短，长的文本输出问答系统可能超时。
     *
     * @param apiKey key
     */
    public OpenAiClient(String apiKey) {
        this.apiKey = apiKey;
        this.okHttpClient(30, 30, 30, null);
    }

    /**
     * 创建OpenAiClient，使用默认的超时时间
     * 注意当超时时间过短，长的文本输出问答系统可能超时。
     *
     * @param apiKey key
     * @param proxy  网络代理
     */
    public OpenAiClient(String apiKey, Proxy proxy) {
        this.apiKey = apiKey;
        this.proxy = proxy;
        this.okHttpClient(30, 30, 30, proxy);
    }

    private OpenAiClient(Builder builder) {
        if (StrUtil.isBlank(builder.apiKey)) {
            throw new RuntimeException();
        }
        apiKey = builder.apiKey;

        apiHost = builder.apiHost;

        connectTimeout = builder.connectTimeout;

        writeTimeout = builder.writeTimeout;

        readTimeout = builder.readTimeout;
        proxy = builder.proxy;
        this.okHttpClient(connectTimeout, writeTimeout, readTimeout, proxy);

    }

    /**
     * 创建 OkHttpClient，自定义超时时间和拦截器
     *
     * @param connectTimeout 默认30秒
     * @param writeTimeout   默认30秒
     * @param readTimeout    默认30秒
     */
    private void okHttpClient(long connectTimeout, long writeTimeout, long readTimeout, Proxy proxy) {

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        client.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        client.readTimeout(readTimeout, TimeUnit.SECONDS);

        this.okHttpClient = client.build();
    }

    /**
     * 问答接口 stream 形式
     *
     * @param completion          open ai 参数
     * @param eventSourceListener sse监听器
     */
    @SuppressWarnings({ "deprecation", "unused" })
	public void streamCompletions(Completion completion, EventSourceListener eventSourceListener) {

        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(completion);
            Request request = new Request.Builder()
                    .url(this.apiHost + "v1/completions")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .header("Authorization", "Bearer " + this.apiKey)
                    .build();
            //创建事件
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求参数解析异常：{0}", e);
        }
    }

    /**
     * 同步回答
     *
     * @param completion
     * @return
     */
    @SuppressWarnings({ "deprecation", "unused" })
    public Response chatCompletions(ChatCompletion completion) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(completion);
            Request request = new Request.Builder()
                    .url(this.apiHost + "v1/chat/completions")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .header("Authorization", "Bearer " + this.apiKey)
                    .build();

            return this.okHttpClient.newCall(request).execute() ;

        } catch (Exception e) {
            log.error("请求参数解析异常：{0}", e);
        }
        return null;
    }

    /**
     * 流式输出，最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     *
     * @param chatCompletion      问答参数
     * @param eventSourceListener sse监听器
     */
    @SuppressWarnings({ "deprecation", "unused" })
	public void streamChatCompletion(ChatCompletion chatCompletion, EventSourceListener eventSourceListener) {

        log.debug("open host = {} , key = {}" , apiHost , apiKey);

        if (!chatCompletion.isStream()) {
            chatCompletion.setStream(true);
        }
        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(chatCompletion);

            log.debug("requestBody = {}" , requestBody);

            Request request = new Request.Builder()
                    .url(apiHost + "v1/chat/completions")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .header("Authorization", "Bearer " + apiKey)
                    .build();

            //创建事件
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);

        } catch (Exception e) {
            log.error("请求参数解析异常：{0}", e);
        }
    }

    /**
     * 构造
     *
     * @return
     */
    public static OpenAiClient.Builder builder() {
        return new OpenAiClient.Builder();
    }

    @Data
    public static final class Builder {
        private @NotNull String apiKey;
        /**
         * api请求地址，结尾处有斜杠
         *
         */
        private String apiHost;
        private long connectTimeout;
        private long writeTimeout;
        private long readTimeout;
        private Proxy proxy;

        public Builder() {
        }

        public Builder apiKey(@NotNull String val) {
            apiKey = val;
            return this;
        }

        /**
         * @param val api请求地址，结尾处有斜杠
         * @return
         */
        public Builder apiHost(String val) {
            apiHost = val;
            return this;
        }

        public Builder connectTimeout(long val) {
            connectTimeout = val;
            return this;
        }

        public Builder writeTimeout(long val) {
            writeTimeout = val;
            return this;
        }

        public Builder readTimeout(long val) {
            readTimeout = val;
            return this;
        }

        public Builder proxy(Proxy val) {
            proxy = val;
            return this;
        }

        public OpenAiClient build() {
            return new OpenAiClient(this);
        }
    }
}
