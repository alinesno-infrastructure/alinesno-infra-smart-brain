package com.alinesno.infra.smart.assistant.role.llm;

import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.adapter.event.StreamStoreMessagePublisher;
import com.alinesno.infra.smart.assistant.api.ModelNodeDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.exception.RateLimitException;
import com.alinesno.infra.smart.im.service.IMessageReferenceService;
import com.alinesno.infra.smart.im.service.IMessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Data
public class BaseModelAdapter {

    protected static final String MESSAGE_ID = "messageId";

    private IndustryRoleEntity role ;  // 角色信息

    private MessageTaskInfo taskInfo ;  // 任务信息

    private ModelNodeDto node ;  // 节点信息

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    @Autowired
    protected StreamStoreMessagePublisher streamStoreMessagePublisher ;  // 不保存入库的消息

    @Autowired
    protected IMessageService messageService;

    @Autowired
    protected IMessageReferenceService messageReferenceService ;

    /**
     * 消息提示
     * @param newMsg
     * @param status
     */
    public void eventModelMessage(String newMsg , String status) {

        if(newMsg == null || newMsg.isEmpty()){
            return ;
        }

        FlowStepStatusDto stepDto = new FlowStepStatusDto() ;
        stepDto.setMessage(newMsg) ;
        stepDto.setStepId(node.getId()) ;
        stepDto.setStatus(status) ;
        stepDto.setPrint(node.isPrint());

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null ,
                role,
                taskInfo,
                taskInfo.getTraceBusId());
    }

    protected CompletableFuture<AiMessage> getAiChatResultAsync(Llm llm,
                                                                IndustryRoleEntity role,
                                                                String prompt,
                                                                MessageTaskInfo taskInfo) {

        CompletableFuture<AiMessage> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        final MessageTaskInfo localTaskInfo = taskInfo;
        long startTime = System.currentTimeMillis();

        try {
            llm.chatStream(prompt, (context, response) -> {
                AiMessage message = response.getMessage();

                System.out.println(">>>> " + message);

                FlowStepStatusDto stepDto = new FlowStepStatusDto();
                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(node.getId());
                stepDto.setStatus(AgentConstants.STEP_PROCESS);
                stepDto.setPrint(node.isPrint());

                if(StringUtils.isNotBlank(message.getContent())) {
                    stepDto.setFlowChatText(message.getContent());
                }

                if(StringUtils.isNotBlank(message.getReasoningContent())){
                    stepDto.setFlowReasoningText(message.getReasoningContent());
                }

                localTaskInfo.setFlowStep(stepDto);

                try {
                    boolean isEnd = message.getStatus() == MessageStatus.END;
                    if (isEnd) {
                        outputStr.set(message.getFullContent());
                        stepDto.setStatus(AgentConstants.STEP_FINISH);
                    }

                    streamMessagePublisher.doStuffAndPublishAnEvent(null, role, localTaskInfo, localTaskInfo.getTraceBusId());

                    if (isEnd) {
                        future.complete(message);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }

    public CompletableFuture<AiMessage> getSingleAiChatResultAsync(Llm llm,
                                                                   IndustryRoleEntity role,
                                                                   String prompt,
                                                                   MessageTaskInfo taskInfo,
                                                                   long messageId) {

        CompletableFuture<AiMessage> future = new CompletableFuture<>();

        try {
            long start = System.currentTimeMillis();

            llm.chatStream(prompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext context, AiMessageResponse response) {
                    log.debug("Received chunk after {}ms", System.currentTimeMillis()-start);

                    AiMessage message = response.getMessage();

                    if(StringUtils.isNotBlank(message.getReasoningContent())){
                        taskInfo.setReasoningText(message.getReasoningContent());
                    } else {
                        taskInfo.setReasoningText(StringUtils.EMPTY);
                    }

                    try {
                        boolean isEnd = message.getStatus() == MessageStatus.END;
                        streamMessagePublisher.doStuffAndPublishAnEvent(message.getContent(),
                                role,
                                taskInfo,
                                taskInfo.getTraceBusId(),
                                messageId);

                        if (isEnd) {
                            message.addMetadata(MESSAGE_ID, messageId);
                            future.complete(message);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public void onFailure(ChatContext context, Throwable throwable) {
                    log.error("Stream processing failed", throwable);

                    // 处理限流情况
                    if (throwable != null && throwable.getMessage().contains("429")) {
                        taskInfo.setErrorMessage("请求过于频繁，请稍后再试");

                        // 创建自定义限流异常
                        RateLimitException rateLimitEx = new RateLimitException("API请求频率超限(429 Too Many Requests)", throwable);

                        // 设置重试建议时间（可根据实际情况调整）
                        rateLimitEx.setRetryAfterSeconds(60);

                        future.completeExceptionally(rateLimitEx);
                    } else {
                        // 其他异常情况
                        future.completeExceptionally(throwable);
                    }
                }

            });

        } catch (Exception e) {
            log.error(e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }

}
