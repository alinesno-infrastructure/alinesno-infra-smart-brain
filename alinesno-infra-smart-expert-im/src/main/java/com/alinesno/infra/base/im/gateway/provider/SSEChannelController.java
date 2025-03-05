package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.base.im.utils.MessageFormatter;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.event.StreamMessageEvent;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.im.service.ITaskService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.IOException;
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

//    @Autowired
//    private IMessageService messageService ;

    /**
     * 监控流消息发送到前端
     * @param event
     */
    @EventListener
    public void handleCustomEvent(StreamMessageEvent event) {

        IndustryRoleEntity role = event.getRole() ;
        MessageTaskInfo info = event.getTaskInfo() ;

        long channelId = info.getChannelId() ;
        String msg = event.getMessage() == null ? "" : event.getMessage() ;

        String reasoningText = MessageFormatter.getSafeString(info.getReasoningText()) ; // info.getReasoningText() == null || "null".equals(info.getReasoningText())) ? "" : info.getReasoningText() ;
        FlowStepStatusDto flowStepDto = info.getFlowStep() ; // MessageFormatter.getSafeString(info.getFlowStepMessage()) ; // (info.getFlowStepMessage() == null || "null".equals(info.getFlowStepMessage())) ? "" : info.getFlowStepMessage() ;

        long bId = event.getBId() ;

        log.debug("Received llm stream message event , msg = {} , channelId = {} " , event.getMessage() , channelId);

        final SseEmitter emitter = service.getConn(channelId + "");
        CompletableFuture.runAsync(() -> {

            ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, bId) ;
            msgDto.setLoading(false);
            msgDto.setChatText(msg);
            msgDto.setLlmStream(true);
            msgDto.setReasoningText(reasoningText);  // 推理内容
            msgDto.setFlowStep(flowStepDto);  // 执行流程节点信息
            msgDto.setRoleType(StringUtils.isNotEmpty(info.getRoleType())?info.getRoleType():"agent");
            msgDto.setContentReferenceArticle(info.getContentReferenceArticle());



            try {
                emitter.send(msgDto) ;

                // 保存记录到数据库中
                // messageService.saveMessage(role , info, msg , msgDto) ;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

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
