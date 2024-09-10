package com.alinesno.infra.base.im.service;

import com.alinesno.infra.base.im.dto.ChatMessageDto;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

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
     * 关闭与指定客户端的SSE连接.
     *
     * @param clientId 客户端ID，不能为空.
     */
    void closeConn(@NotBlank String clientId);

    /**
     * 关闭所有SSE连接.
     */
    void closeAllConn();
}
