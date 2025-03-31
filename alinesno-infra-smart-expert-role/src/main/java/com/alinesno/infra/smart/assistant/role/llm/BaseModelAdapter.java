package com.alinesno.infra.smart.assistant.role.llm;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.api.ModelNodeDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.IMessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Data
public class BaseModelAdapter {

    private IndustryRoleEntity role ;  // 角色信息

    private MessageTaskInfo taskInfo ;  // 任务信息

    private ModelNodeDto node ;  // 节点信息

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    @Autowired
    protected IMessageService messageService;

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
        stepDto.setMessage("任务进行中...") ;
        stepDto.setStepId(node.getId()) ;
        stepDto.setStatus(status) ;
        stepDto.setFlowChatText(newMsg) ;
        stepDto.setPrint(node.isPrint());

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null ,
                role,
                taskInfo,
                taskInfo.getTraceBusId());
    }

    protected CompletableFuture<AiMessage> getAiChatResultAsync(Llm llm,
                                                                IndustryRoleEntity role  ,
                                                                String prompt,
                                                                MessageTaskInfo taskInfo ,
                                                                String oneChatId) {

        CompletableFuture<AiMessage> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        // 创建一个 final 局部变量来持有 taskInfo 的引用
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

                if(com.alinesno.infra.common.core.utils.StringUtils.isNotBlank(message.getContent())) {
                    stepDto.setFlowChatText(message.getContent());
                }

                if(com.alinesno.infra.common.core.utils.StringUtils.isNotBlank(message.getReasoningContent())){
                    stepDto.setFlowReasoningText(message.getReasoningContent());
                }

//                stepDto.setPrint(true);

                synchronized (localTaskInfo) {
                    localTaskInfo.setFlowStep(stepDto);
                }

                try {
                    boolean isEnd = false;
                    synchronized (localTaskInfo) {
                        if (message.getStatus() == MessageStatus.END) {
                            outputStr.set(message.getFullContent());
                            stepDto.setStatus(AgentConstants.STEP_FINISH);
                            isEnd = true;
                        }
                    }

                    streamMessagePublisher.doStuffAndPublishAnEvent(null, role, localTaskInfo, localTaskInfo.getTraceBusId());

                    // TODO 是否在这里插入完成的消息？
                    if (isEnd) {
                        future.complete(message);
                    }
                } catch (Exception e) {
                    // 处理发布事件时的异常
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            });
        } catch (Exception e) {
            // 处理 chatStream 方法的异常
            log.error(e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }


}
