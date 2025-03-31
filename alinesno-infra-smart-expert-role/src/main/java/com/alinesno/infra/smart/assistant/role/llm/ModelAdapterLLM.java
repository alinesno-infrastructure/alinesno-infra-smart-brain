package com.alinesno.infra.smart.assistant.role.llm;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * AgentFlexLLM
 */
@Slf4j
@Service
public class ModelAdapterLLM extends BaseModelAdapter {

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
    public String processStream(Llm llm , IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo , boolean isSave) {

        long workflowId = IdUtil.getSnowflakeNextId() ;

//        CompletableFuture<AiMessage> future = getAiChatResultAsync(llm, role, prompt, taskInfo, workflowId+"") ;
        CompletableFuture<AiMessage> future = getAiChatResultAsync(llm, role, prompt, taskInfo, getNode().getId()) ;

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
        return processStream(llm, role, prompt, taskInfo, false) ;
    }

}
