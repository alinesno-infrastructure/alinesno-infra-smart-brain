package com.alinesno.infra.base.im.service.impl;

import cn.hutool.core.lang.Assert;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.utils.AgentUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SSEServiceImpl implements ISSEService {

    private static final Map<String, SseEmitter> SSE_CACHE = new ConcurrentHashMap<>();

    @Override
    public SseEmitter getConn(@NotBlank String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);

        log.trace("获取SSE连接，clientId = {} ,sseEmitter = {}", clientId , sseEmitter);

        if (sseEmitter != null) {
            return sseEmitter;
        } else {
            // 设置连接超时时间，需要配合配置项 spring.mvc.async.request-timeout: 600000 一起使用
            final SseEmitter emitter = getSseEmitter(clientId);

            // 注册异常回调，调用 emitter.completeWithError() 触发
            emitter.onError(throwable -> {
                log.error("连接已异常，正准备关闭，clientId = {}", clientId, throwable);
                SSE_CACHE.remove(clientId);
            });

            SSE_CACHE.put(clientId, emitter);

            return emitter;
        }
    }

    @NotNull
    private static SseEmitter getSseEmitter(String clientId) {
        final SseEmitter emitter = new SseEmitter(0L);
        // 注册超时回调，超时后触发
        emitter.onTimeout(() -> {
            log.trace("连接已超时，正准备关闭，clientId = {}", clientId);
            SSE_CACHE.remove(clientId);
        });
        // 注册完成回调，调用 emitter.complete() 触发
        emitter.onCompletion(() -> {
            log.trace("连接已关闭，正准备释放，clientId = {}", clientId);
            SSE_CACHE.remove(clientId);
            log.trace("连接已释放，clientId = {}", clientId);
        });
        return emitter;
    }

    /**
     * 模拟类似于 chatGPT 的流式推送回答
     * @param clientId 客户端 id
     * @throws IOException 异常
     */
    @Override
    public void send(@NotBlank String clientId , ChatMessageDto message) throws IOException {
        final SseEmitter emitter = SSE_CACHE.get(clientId);      // 推流内容到客户端

        Assert.notNull(emitter, "客户端不存在，clientId = " + clientId);
        Assert.notNull(message, "推送消息为空");

        emitter.send(message) ;
        emitter.send("[DONE]");

        // 结束推流
        // emitter.complete();
    }

    @Override
    public void send(@NotBlank String clientId , String message) throws IOException {
        final SseEmitter emitter = SSE_CACHE.get(clientId);      // 推流内容到客户端
        if(emitter == null){
            log.trace("客户端：【{}】 断开成功，当前剩余客户端总数为【{}】",clientId,SSE_CACHE.size());
            return ;
        }

        Assert.notNull(message, "推送消息为空");

        emitter.send(message) ;
        emitter.send("[DONE]");

        // 结束推流
        // emitter.complete();
    }

    @Override
    public void sendDone(String clientId) throws IOException {
        final SseEmitter emitter = SSE_CACHE.get(clientId);      // 推流内容到客户端
        if(emitter == null){
            log.trace("客户端：【{}】 断开成功，当前剩余客户端总数为【{}】",clientId,SSE_CACHE.size());
            return ;
        }
        emitter.send("[DONE]");
    }

    /**
     * 定时任务 用于测试后端推送的数据
     */
    @Scheduled(fixedDelay = 60, initialDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void job() {
        if (!SSE_CACHE.isEmpty()) {
            log.trace("当前客户端总数为：{}", SSE_CACHE.size());

            String msg = "ping";
            Iterator<Map.Entry<String, SseEmitter>> iterator = SSE_CACHE.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, SseEmitter> entry = iterator.next();
                SseEmitter emitter = entry.getValue();
                try {
                    log.trace("推送心跳数据，clientId:{} , msg:{}", entry.getKey(), msg);
                    emitter.send(SseEmitter.event()
                            .reconnectTime(1000)
                            .id(entry.getKey())
                            .data(msg));
                } catch (Exception e) {
                    log.trace("客户端【{}】连接异常，已自动移除，原因：{}", entry.getKey(), e.getMessage());
                    iterator.remove(); // 先从缓存中移除
                    try {
                        emitter.completeWithError(e); // 标记为错误完成
                    } catch (Exception ex) {
                        // 忽略complete时的异常
                    }
                }
            }
        }
    }

    @Override
    public void closeConn(@NotBlank String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null) {
            sseEmitter.complete();
            log.info("客户端：【{}】 断开成功，当前剩余客户端总数为【{}】",clientId,SSE_CACHE.size());
        }
    }

    @Override
    public void closeAllConn() {
        SSE_CACHE.forEach((clientId, sseEmitter) -> sseEmitter.complete());
    }

    @SneakyThrows
    @Override
    public void sendQueueMessage(String clientId , List<MessageTaskInfo> messageList) {
        final SseEmitter emitter = SSE_CACHE.get(clientId);      // 推流内容到客户端

      if(messageList != null)  {
          for (MessageTaskInfo message : messageList) {
              ChatMessageDto chatMessage = AgentUtils.genSSEChatMessageDto() ;

              chatMessage.setName(message.getRoleDto().getRoleName());
              chatMessage.setIcon(message.getRoleDto().getRoleAvatar());
              chatMessage.setChatText(message.getText());

              emitter.send(chatMessage);
          }
      }

        emitter.send("[DONE]");
    }

    @SneakyThrows
    @Override
    public void sendNotDone(String clientId, String message) {
        final SseEmitter emitter = SSE_CACHE.get(clientId);      // 推流内容到客户端

        Assert.notNull(emitter, "客户端不存在，clientId = " + clientId);
        Assert.notNull(message, "推送消息为空");

        emitter.send(message) ;
    }

}
