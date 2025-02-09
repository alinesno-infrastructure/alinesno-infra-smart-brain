package com.alinesno.infra.smart.assistant.role.llm;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.util.StringUtil;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.role.llm.adapter.MessageManager;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.service.IMessageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Semaphore;

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

    @SneakyThrows
    public String chatComponent(Llm llm , String prompt) {
        return llm.chat(prompt);
    }

    /**
     * 流式任务
     * @param role
     * @param prompt
     * @param taskInfo
     */
    @SneakyThrows
    public void processStream(Llm llm , IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {

        long workflowId = IdUtil.getSnowflakeNextId() ;

        llm.chatStream(prompt, (context, response) -> {
            AiMessage message = response.getMessage();

            log.debug(">>>> 推理内容: {}" , message.getFullReasoningContent());
            log.debug(">>>> 结果内容: {}" , message.getFullContent());

            if(StringUtil.hasText(message.getReasoningContent())){
                streamMessagePublisher.doStuffAndPublishAnEvent(message.getReasoningContent() , role, taskInfo, workflowId);
            }

            if(StringUtil.hasText(message.getContent())){
                streamMessagePublisher.doStuffAndPublishAnEvent(message.getContent() , role, taskInfo, workflowId);
            }

            MessageStatus status =  message.getStatus() ;
            if(status == MessageStatus.END){  // 结束

                MessageEntity entity = new MessageEntity();

                entity.setTraceBusId(taskInfo.getTraceBusId());
                entity.setId(workflowId) ;
                entity.setContent(message.getFullContent()) ;
                entity.setFormatContent(message.getFullContent());
                entity.setName(role.getRoleName());

                entity.setRoleType("agent");
                entity.setReaderType("html");

                entity.setAddTime(new Date());
                entity.setIcon(role.getRoleAvatar());

                entity.setChannelId(taskInfo.getChannelId()) ;
                entity.setRoleId(role.getId()) ;

                messageService.save(entity);

                streamMessagePublisher.doStuffAndPublishAnEvent("流式任务完成.",
                        role ,
                        taskInfo ,
                        IdUtil.getSnowflakeNextId()) ;
            }

        });

//        MessageManager msgManager = new MessageManager(10);
//
//        for(PromptMessage m : messages) {
//            com.alibaba.dashscope.common.Message msg = com.alibaba.dashscope.common.Message.builder()
//                    .role(m.getRole())
//                    .content(m.getContent())
//                    .build();
//            msgManager.add(msg);
//        }
//
//        processStreamCallback(role, taskInfo, msgManager);

    }

    private void processStreamCallback(IndustryRoleEntity role, MessageTaskInfo taskInfo, MessageManager msgManager) throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();
        long workflowId = IdUtil.getSnowflakeNextId() ; // taskInfo.getWorkflowRecordId() ;

        msgManager.setTraceBusId(taskInfo.getTraceBusId());
        msgManager.setWorkflowId(workflowId);
        msgManager.setChannelId(taskInfo.getChannelId());

//        qianWenLLM.getGeneration(msgManager, new ResultCallback<>() {
//            @SneakyThrows
//            @Override
//            public void onEvent(GenerationResult message) {
//
//                String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
//                String finishReason = message.getOutput().getChoices().get(0).getFinishReason() ;
//
//                log.info("Received message: {}", JsonUtils.toJson(message));
//
//                if (finishReason != null && finishReason.equals("stop")) {
//                    msg = "[DONE]" ;
//                    semaphore.release();
//                }else{
//                    fullContent.append(msg);
//                }
//
//                streamMessagePublisher.doStuffAndPublishAnEvent(msg , role, taskInfo, workflowId);
//            }
//
//            @Override
//            public void onError(Exception err) {
//                log.error("Exception occurred: {}", err.getMessage());
//                semaphore.release();
//            }
//
//            @Override
//            public void onComplete() {
//                log.info("Completed");
//                semaphore.release();
//                log.info("Full content: \n{}", fullContent);
//
//                MessageEntity entity = new MessageEntity();
//
//                entity.setTraceBusId(taskInfo.getTraceBusId());
//                entity.setId(msgManager.getWorkflowId());
//                entity.setContent(fullContent.toString()) ;
//                entity.setFormatContent(fullContent.toString());
//                entity.setName(role.getRoleName());
//
//                entity.setRoleType("agent");
//                entity.setReaderType("html");
//
//                entity.setAddTime(new Date());
//                entity.setIcon(role.getRoleAvatar());
//
//                entity.setChannelId(msgManager.getChannelId());
//                entity.setRoleId(role.getId()) ;
//
//                messageService.save(entity);
//
//                streamMessagePublisher.doStuffAndPublishAnEvent("流式任务完成.",
//                        getRole() ,
//                        getTaskInfo() ,
//                        IdUtil.getSnowflakeNextId()) ;
//            }
//        }) ;
//
//        semaphore.acquire();

    }

}
