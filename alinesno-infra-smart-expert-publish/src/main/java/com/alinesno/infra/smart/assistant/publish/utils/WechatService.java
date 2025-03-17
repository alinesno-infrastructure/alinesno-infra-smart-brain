package com.alinesno.infra.smart.assistant.publish.utils;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import redis.clients.jedis.Jedis;
import java.io.IOException;

@Slf4j
@Component
@Data
public class WechatService {

    @Value("${spring.data.redis.host:}")
    private String redisHost = "localhost" ;

    @Value("${spring.data.redis.port:}")
    private int redisPort = 6379;

    @Value("${spring.data.redis.password:}")
    private String redisPassword ;

    private String appId ;
    private String appSecret ;
    private String accessTokenKey;

    /**
     * 获取 accessToken，优先从 Redis 缓存中获取
     * @return
     */
    public String getAccessToken() {

        Assert.notNull(appId , "appId is null") ;
        Assert.notNull(appSecret , "appSecret is null") ;
        Assert.notNull(redisHost , "redisHost is null") ;
        Assert.notNull(accessTokenKey , "accessTokenKey is null") ;

        Jedis jedis = new Jedis(redisHost, redisPort);

        try (jedis) {
            if(redisPassword != null){
                jedis.auth(redisPassword);
            }
            String cachedToken = jedis.get(getAccessTokenKey());
            if (cachedToken != null) {
                return cachedToken;
            }

            // 若缓存中没有，则重新获取
            OkHttpClient client = new OkHttpClient();
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + getAppId() + "&secret=" + getAppSecret() ;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(responseData);
                    String accessToken = jsonObject.getString("access_token");
                    int expiresIn = jsonObject.getIntValue("expires_in");

                    log.debug("获取 accessToken:{} 成功，过期时间：{}", accessToken, expiresIn);

                    // 将 accessToken 存入 Redis 缓存，设置过期时间比实际过期时间稍短
                    jedis.setex(getAccessTokenKey(), expiresIn - 60, accessToken);
                    return accessToken;
                }
            }
        } catch (IOException e) {
            log.error("发送消息时发生异常", e);
        }
        return null;
    }

    /**
     * 向指定用户发送文本消息
     * @param openId
     * @param content
     * @return
     */
    public String sendTextMessageToUser(String openId, String content ) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return "获取 accessToken 失败";
        }

        OkHttpClient client = new OkHttpClient();
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;

        JSONObject message = new JSONObject();
        message.put("touser", openId);
        message.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content", content);
        message.put("text", text);

        System.out.println(message.toJSONString());

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), message.toJSONString());
        Request request = new Request.Builder()
               .url(url)
               .post(body)
               .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return "消息发送失败";
    }

}