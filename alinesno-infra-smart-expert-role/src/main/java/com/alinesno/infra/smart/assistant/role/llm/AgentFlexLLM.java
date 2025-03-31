package com.alinesno.infra.smart.assistant.role.llm;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.service.IMessageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AgentFlexLLM
 */
@Deprecated
@Slf4j
@Service
public class AgentFlexLLM {

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String qianWenKey;

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    @Autowired
    protected IMessageService messageService;

    public String chatComponent(Llm llm , String prompt) {
        try{
            return llm.chat(prompt);
        }catch (Exception e){
            log.error("LLM chat error" , e);
            return "LLM chat error: " + e.getMessage();
        }
    }

    /**
     * 流式任务完成并保存消息，用于最后回答
     *
     * @param role
     * @param prompt
     * @param taskInfo
     * @return
     */
    @SneakyThrows
    public String processStreamFinish(Llm llm , IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo , boolean isSave) {

        long workflowId = IdUtil.getSnowflakeNextId() ;

        CompletableFuture<AiMessage> future = getAiChatResultAsync(llm, role, prompt, taskInfo, workflowId+"") ;

        AiMessage message = future.get();
        log.debug("output = {}" , message.getFullContent());

        if(isSave){
            MessageEntity entity = new MessageEntity();

            entity.setTraceBusId(taskInfo.getTraceBusId());
            entity.setId(workflowId) ;
            entity.setContent(message.getFullContent()) ;
            entity.setReasoningContent(message.getFullReasoningContent());
            entity.setFormatContent(message.getFullContent());
            entity.setName(role.getRoleName());

            entity.setRoleType("agent");
            entity.setReaderType("html");

            entity.setAddTime(new Date());
            entity.setIcon(role.getRoleAvatar());

            entity.setChannelId(taskInfo.getChannelId()) ;
            entity.setRoleId(role.getId()) ;

            messageService.save(entity);
        }

        return message.getFullContent() ;

    }

    /**
     * 流式任务，不保存消息
     *
     * @param role
     * @param prompt
     * @param taskInfo
     * @return
     */
    @SneakyThrows
    public String processStream(Llm llm , IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {
        return processStreamFinish(llm, role, prompt, taskInfo, false) ;
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
                stepDto.setStepId(oneChatId);
                stepDto.setStatus(AgentConstants.STEP_PROCESS);

                if(com.alinesno.infra.common.core.utils.StringUtils.isNotBlank(message.getContent())) {
                    stepDto.setFlowChatText(message.getContent());
                }

                if(com.alinesno.infra.common.core.utils.StringUtils.isNotBlank(message.getReasoningContent())){
                    stepDto.setFlowReasoningText(message.getReasoningContent());
                }

                stepDto.setPrint(true);

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
