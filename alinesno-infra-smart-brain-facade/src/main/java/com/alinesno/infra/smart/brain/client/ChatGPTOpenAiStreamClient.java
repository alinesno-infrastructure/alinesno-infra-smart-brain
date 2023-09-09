package com.alinesno.infra.smart.brain.client;

import java.net.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.constant.OpenAIConst;
import com.unfbx.chatgpt.entity.billing.CreditGrantsResponse;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.common.OpenAiResponse;
import com.unfbx.chatgpt.entity.completions.Completion;
import com.unfbx.chatgpt.exception.BaseException;
import com.unfbx.chatgpt.exception.CommonError;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 描述： open ai 客户端
 * @author luoxiaodong
 * @version 1.0.0
 */
public class ChatGPTOpenAiStreamClient {

    private static final Logger log = LoggerFactory.getLogger(ChatGPTOpenAiStreamClient.class) ;

    private String apiKey;
    /**
     * 自定义api host使用builder的方式构造client
     */
    private String apiHost = OpenAIConst.OPENAI_HOST;

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
    public ChatGPTOpenAiStreamClient(String apiKey, long connectTimeout, long writeTimeout, long readTimeout, Proxy proxy) {
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
    public ChatGPTOpenAiStreamClient(String apiKey, long connectTimeout, long writeTimeout, long readTimeout) {
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
    public ChatGPTOpenAiStreamClient(String apiKey) {
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
    public ChatGPTOpenAiStreamClient(String apiKey, Proxy proxy) {
        this.apiKey = apiKey;
        this.proxy = proxy;
        this.okHttpClient(30, 30, 30, proxy);
    }

    private ChatGPTOpenAiStreamClient(Builder builder) {
        if (StrUtil.isBlank(builder.apiKey)) {
            throw new BaseException(CommonError.API_KEYS_NOT_NUL);
        }
        apiKey = builder.apiKey;

        if (StrUtil.isBlank(builder.apiHost)) {
            builder.apiHost = OpenAIConst.OPENAI_HOST;
        }
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
     * @see ConsoleEventSourceListener
     */
    @SuppressWarnings({ "deprecation", "unused" })
	public void streamCompletions(Completion completion, EventSourceListener eventSourceListener) {
        if (Objects.isNull(eventSourceListener)) {
            log.error("参数异常：EventSourceListener不能为空，可以参考：com.unfbx.chatgpt.sse.ConsoleEventSourceListener");
            throw new BaseException(CommonError.PARAM_ERROR);
        }
        if (StrUtil.isBlank(completion.getPrompt())) {
            log.error("参数异常：Prompt不能为空");
            throw new BaseException(CommonError.PARAM_ERROR);
        }
        if (!completion.isStream()) {
            completion.setStream(true);
        }
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
     * 问答接口-简易版
     *
     * @param question            请求参数
     * @param eventSourceListener sse监听器
     * @see ConsoleEventSourceListener
     */
    public void streamCompletions(String question, EventSourceListener eventSourceListener) {
        Completion q = Completion.builder()
                .prompt(question)
                .stream(true)
                .build();
        this.streamCompletions(q, eventSourceListener);
    }

    /**
     * 流式输出，最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     *
     * @param chatCompletion      问答参数
     * @param eventSourceListener sse监听器
     * @see ConsoleEventSourceListener
     */
    @SuppressWarnings({ "deprecation", "unused" })
	public void streamChatCompletion(ChatCompletion chatCompletion, EventSourceListener eventSourceListener) {

        log.debug("open host = {} , key = {}" , apiHost , apiKey);

        if (Objects.isNull(eventSourceListener)) {
            log.error("参数异常：EventSourceListener不能为空，可以参考：com.unfbx.chatgpt.sse.ConsoleEventSourceListener");
            throw new BaseException(CommonError.PARAM_ERROR);
        }
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
     * 流式输出，最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     *
     * @param messages            问答列表
     * @param eventSourceListener sse监听器
     * @see ConsoleEventSourceListener
     */
    public void streamChatCompletion(List<Message> messages, EventSourceListener eventSourceListener) {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(messages)
                .stream(true)
                .build();
        this.streamChatCompletion(chatCompletion, eventSourceListener);
    }

    /**
     * OpenAi账户余额查询
     *
     * @return 余额信息
     */
    @SneakyThrows
    @SuppressWarnings("rawtypes")
    public CreditGrantsResponse creditGrants() {
        
        Request request = new Request.Builder()
                .url(this.apiHost + "dashboard/billing/credit_grants")
                .header(Header.AUTHORIZATION.getValue(), "Bearer " + this.apiKey)
        	    .build();

        log.debug("okHttpClient = {}" , this.okHttpClient) ;
        
		Response response = this.okHttpClient.newCall(request).execute();

        assert response.body() != null;
        
        String body = response.body().string() ;
        log.info("调用查询余额请求返回值：{}", body);
        
        if (!response.isSuccessful()) {
            if (response.code() == CommonError.OPENAI_AUTHENTICATION_ERROR.code()
                    || response.code() == CommonError.OPENAI_LIMIT_ERROR.code()
                    || response.code() == CommonError.OPENAI_SERVER_ERROR.code()) {
            	
                OpenAiResponse openAiResponse = JSONUtil.toBean(body, OpenAiResponse.class);
                log.error(openAiResponse.getError().getMessage());
                
                throw new BaseException(openAiResponse.getError().getMessage());
            }
            String errorMsg = response.body().string();
            log.error("询余额请求异常：{}", errorMsg);
            OpenAiResponse openAiResponse = JSONUtil.toBean(errorMsg, OpenAiResponse.class);
            if (Objects.nonNull(openAiResponse.getError())) {
                log.error(openAiResponse.getError().getMessage());
                throw new BaseException(openAiResponse.getError().getMessage());
            }
            throw new BaseException(CommonError.RETRY_ERROR);
        }
        ObjectMapper mapper = new ObjectMapper();
        // 读取Json 返回值
        return mapper.readValue(body, CreditGrantsResponse.class);
    }

    /**
     * 构造
     *
     * @return
     */
    public static ChatGPTOpenAiStreamClient.Builder builder() {
        return new ChatGPTOpenAiStreamClient.Builder();
    }

    public static final class Builder {
        private @NotNull String apiKey;
        /**
         * api请求地址，结尾处有斜杠
         *
         * @see com.unfbx.chatgpt.constant.OpenAIConst
         */
        private String apiHost;
        private long connectTimeout;
        private long writeTimeout;
        private long readTimeout;
        private Proxy proxy;

        public Builder() {
            apiKey = null;
        }

        public Builder apiKey(@NotNull String val) {
            apiKey = val;
            return this;
        }

        /**
         * @param val api请求地址，结尾处有斜杠
         * @return
         * @see com.unfbx.chatgpt.constant.OpenAIConst
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

        public ChatGPTOpenAiStreamClient build() {
            return new ChatGPTOpenAiStreamClient(this);
        }
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
