package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * SSE服务接口，用于处理服务器发送事件（SSE）的相关操作.
 */
public interface ISSEService {

    /**
     * 获取与指定客户端的SSE连接.
     *
     * @param clientId 客户端ID，不能为空.
     * @return SseEmitter 实例，用于向客户端推送数据.
     */
    SseEmitter getConn(@NotBlank String clientId);

    /**
     * 向指定客户端发送数据.
     *
     * @param clientId 客户端ID，不能为空.
     * @throws IOException 如果在发送过程中发生I/O错误，则抛出此异常.
     */
    void send(@NotBlank String clientId , ChatMessageDto message) throws IOException;

    /**
     * 直接发送文本消息
     * @param clientId
     * @param message
     * @throws IOException
     */
    void send(@NotBlank String clientId , String message) throws IOException;

    /**
     * 发送完成信息
     * @param clientId
     * @throws IOException
     */
    void sendDone(@NotBlank String clientId) throws IOException;

    /**
     * 关闭与指定客户端的SSE连接.
     *
     * @param clientId 客户端ID，不能为空.
     */
    void closeConn(@NotBlank String clientId);

    /**
     * 关闭所有SSE连接.
     */
    void closeAllConn();

    /**
     * 连接队列则发送之前未处理的消息
     * @param clientId
     */
    void sendQueueMessage(@NotBlank String clientId, List<MessageTaskInfo> messageList);

    /**
     * 发送未完成的消息
     * @param clientId
     * @param message
     */
    void sendNotDone(String clientId, String message);
}
