package com.alinesno.infra.smart.assistant.role.llm;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
     * 输出内容
     */
    public void outputContent(String answer) {
        streamStoreMessagePublisher.doStuffAndPublishAnEvent(answer == null ? "" : answer,
                getRole(),
                getTaskInfo(),
                getTaskInfo().getTraceBusId()) ;
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

        CompletableFuture<AiMessage> future = getAiChatResultAsync(llm, role, prompt, taskInfo) ; //, getNode().getId()) ;

        AiMessage message = future.get();
        log.debug("output = {}" , message.getFullContent());

        if(isSave){
            MessageEntity entity = getMessageEntity(role, taskInfo, message);

            messageService.save(entity);
        }

        return message.getFullContent() ;

    }

    @NotNull
    private static MessageEntity getMessageEntity(IndustryRoleEntity role, MessageTaskInfo taskInfo, AiMessage message) {
        MessageEntity entity = new MessageEntity();

        entity.setTraceBusId(taskInfo.getTraceBusId());
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

        // 权限配置
        entity.setOrgId(taskInfo.getOrgId());
        entity.setDepartmentId(taskInfo.getDepartmentId());
        entity.setOperatorId(taskInfo.getOperatorId());

        return entity;
    }

    /**
     * 流式任务完成并保存消息，用于最后回答
     *
     * @param role
     * @param prompt
     * @param taskInfo
     * @return
     */
    @Deprecated
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @SneakyThrows
    public String processStreamSingle(Llm llm , IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {
        throw new UnsupportedOperationException("脚本使用过时的方法，请重新调整!");
    }

    // 移除同步等待，返回CompletableFuture而非String
    public CompletableFuture<String> processStreamSingleAsync(Llm llm, IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {
        return getSingleAiChatResultAsync(llm, role, prompt, taskInfo, IdUtil.getSnowflakeNextId())
                .thenApply(aiMessage -> {

                    String messageId = aiMessage.getMetadata(MESSAGE_ID) + "" ;
                    MessageEntity entity = getMessageEntity(role, taskInfo, aiMessage);
                    entity.setId(Long.valueOf(messageId));

                    // 保存消息
                    messageService.save(entity);

                    // 保存消息引用
                    messageReferenceService.saveMessageReference(taskInfo.getDatasetMap() , messageId);

                    return aiMessage.getFullContent();
                });
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
