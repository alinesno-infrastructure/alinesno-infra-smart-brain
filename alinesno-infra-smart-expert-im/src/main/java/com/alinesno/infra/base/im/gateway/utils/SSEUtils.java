package com.alinesno.infra.base.im.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE工具类
  */
@Slf4j
@Component
public class SSEUtils {
 
    private static final Map<String , SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
 
    /**
     * 创建连接
     */
    public SseEmitter connect(String channel) {
        if (sseEmitterMap.containsKey(channel)) {
            SseEmitter sseEmitter =sseEmitterMap.get(channel);
            sseEmitterMap.remove(channel);
            sseEmitter.complete();
        }
        try {
            UUID uuid = UUID.randomUUID();
            String str = uuid.toString();
            String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
 
            // 设置超时时间，0表示不过期。默认不过期
            SseEmitter sseEmitter = new SseEmitter(0L);
            sseEmitter.send(SseEmitter.event().id(temp).data(""));

            // 注册回调
            sseEmitter.onCompletion(completionCallBack(channel));
            sseEmitter.onTimeout(timeoutCallBack(channel));

            sseEmitterMap.put(channel, sseEmitter);
            log.info("创建sse连接完成，当前频道：{}", channel);

            return sseEmitter;
        } catch (Exception e) {
            log.info("创建sse连接异常，当前频道：{}", channel);
        }
        return null;
    }
 
    /**
     * 给指定频道发送消息
     *
     */
    public boolean sendSseMessage(String channel,String messageId, String message) {
        if (sseEmitterMap.containsKey(channel)) {
            SseEmitter sseEmitter = sseEmitterMap.get(channel);
            try {
                sseEmitter.send(SseEmitter.event().id(messageId).data(message));
                log.info("频道{},消息id:{},推送成功:{}", channel,messageId, message);
                return true;
            }catch (Exception e) {
                sseEmitterMap.remove(channel);
                log.info("频道{},消息id:{},推送异常:{}", channel,messageId, e.getMessage());
                sseEmitter.complete();
                return false;
            }
        }else {
            log.info("频道{}未上线", channel);
        }
        return false;
    }
 
    /**
     * 删除连接
     * @param userId
     */
    public void deleteChannel(String userId){
        removeChannel(userId);
    }
 
    private static Runnable completionCallBack(String channel) {
        return () -> {
            log.info("结束sse频道连接：{}", channel);
            removeChannel(channel);
        };
    }
 
    private static Throwable errorCallBack(String channel) {
        log.info("sse频道连接异常：{}", channel);
        removeChannel(channel);
        return new Throwable();
    }
 
    private static Runnable timeoutCallBack(String channel) {
        return () -> {
            log.info("连接sse频道超时：{}", channel);
            removeChannel(channel);
        };
    }
 
    /**
     * 断开
     * @param channel
     */
    public static void removeChannel(String channel){
        if (sseEmitterMap.containsKey(channel)) {
            SseEmitter sseEmitter = sseEmitterMap.get(channel);
            sseEmitterMap.remove(channel);
            sseEmitter.complete();
        }else {
            log.info("频道{} 连接已关闭",channel);
        }
    }
 
    public Map<String, SseEmitter> listSseConnect(){
         return sseEmitterMap;
    }
}