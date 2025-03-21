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
     * 流式任务
     *
     * @param role
     * @param prompt
     * @param taskInfo
     * @return
     */
    @SneakyThrows
    public String processStream(Llm llm , IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {

        long workflowId = IdUtil.getSnowflakeNextId() ;

        CompletableFuture<String> future = getAiChatResultAsync(llm, role, prompt, taskInfo, workflowId+"") ;

        String output = future.get();
        log.debug("output = {}" , output);


        return output ;

    }

    protected CompletableFuture<String> getAiChatResultAsync(Llm llm,
                                                             IndustryRoleEntity role  ,
                                                             String prompt,
                                                             MessageTaskInfo taskInfo ,
                                                             String oneChatId) {

        CompletableFuture<String> future = new CompletableFuture<>();
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

                    if (isEnd) {

                        MessageEntity entity = new MessageEntity();

                        entity.setTraceBusId(taskInfo.getTraceBusId());
                        entity.setId(Long.parseLong(oneChatId)) ;
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

                        future.complete(outputStr.get());
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
