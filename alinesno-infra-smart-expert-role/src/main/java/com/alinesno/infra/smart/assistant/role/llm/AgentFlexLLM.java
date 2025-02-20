package com.alinesno.infra.smart.assistant.role.llm;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.util.StringUtil;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.event.StreamMessagePublisher;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.service.IMessageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

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
     * @param role
     * @param prompt
     * @param taskInfo
     */
    @SneakyThrows
    public void processStream(Llm llm , IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {

        long workflowId = IdUtil.getSnowflakeNextId() ;

        llm.chatStream(prompt, (context, response) -> {
            AiMessage message = response.getMessage();

            if(StringUtil.hasText(message.getReasoningContent())){
                taskInfo.setReasoningText(message.getReasoningContent());
                streamMessagePublisher.doStuffAndPublishAnEvent(null , role, taskInfo, workflowId);
            }

            if(StringUtil.hasText(message.getContent())){
                taskInfo.setReasoningText(null);
                streamMessagePublisher.doStuffAndPublishAnEvent(message.getContent() , role, taskInfo, workflowId);
            }

            MessageStatus status =  message.getStatus() ;
            if(status == MessageStatus.END){  // 结束

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

                streamMessagePublisher.doStuffAndPublishAnEvent("流式任务完成.",
                        role ,
                        taskInfo ,
                        IdUtil.getSnowflakeNextId()) ;
            }

        });

    }

}
