package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.im.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SSEController 类，主要用于处理服务器发送的事件（SSE）的请求。
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/sse/")
public class SSEChannelController {

    @Autowired
    private ISSEService service;

    @Autowired
    private ITaskService taskService;

    /**
     * 建立SSE连接
     * @param clientId
     * @return
     */
    @GetMapping(value = "openConn/{clientId}", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter connect(@PathVariable("clientId") String clientId) {

        final SseEmitter emitter = service.getConn(clientId);
        CompletableFuture.runAsync(() -> {
            try {
                List<MessageTaskInfo> messageList = taskService.getTaskMessage(clientId) ;
                service.sendQueueMessage(clientId , messageList);
            } catch (Exception e) {
                throw new RpcServiceRuntimeException("推送数据异常");
            }
        });

        return emitter;
    }

    /**
     * 关闭SSE连接
     * @param clientId
     * @return
     */
    @GetMapping("closeConn/{clientId}")
    public AjaxResult closeConn(@PathVariable("clientId") String clientId) {
        service.closeConn(clientId);
        return AjaxResult.success("连接已关闭");
    }

    /**
     * 关闭所有SSE连接
     */
    @GetMapping("closeAllConn")
    public AjaxResult closeAllConn() {
        service.closeAllConn();
        return AjaxResult.success("所有连接已关闭");
    }

}
