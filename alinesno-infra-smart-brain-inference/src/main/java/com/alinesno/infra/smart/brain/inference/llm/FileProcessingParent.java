package com.alinesno.infra.smart.brain.inference.llm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.entity.GenerateTaskHistoryEntity;
import com.alinesno.infra.smart.brain.entity.PromptPostsEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.inference.tools.TemplateParser;
import com.alinesno.infra.smart.brain.service.*;
import com.alinesno.infra.smart.brain.utils.CodeBlockParser;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class FileProcessingParent implements IFileProcessingService {

    @Autowired
    protected IPromptPostsService promptPostsService ;

    @Autowired
    protected ILLMApiService llmApiService ;

    protected static ChatCompletion getChatCompletion(List<Message> messages) {
        return ChatCompletion.builder()
                .messages(messages)
                .temperature(0.5)
                .build();
    }

    protected void extractedFinishTask(StringBuilder sb , long startTime , boolean isFinish, GenerateTaskEntity dto) {
        if(isFinish){

            IGenerateTaskService generateTaskService = SpringContext.getBean(IGenerateTaskService.class) ;

            long endTime = System.currentTimeMillis() ;
            int usedTime = (int) ((endTime - startTime));

            log.debug("方法执行时间：" + usedTime + " 秒");

            // 判断是否获取到代码内容(这里返回的结果里面一定要有代码)
            List<TaskContentDto.CodeContent> codeContents =  CodeBlockParser.parseCodeBlocks(sb.toString()) ;

            if(codeContents.isEmpty()){ // 未完成，则重新生成内容

                dto.setAssistantContent(sb.toString());
                dto.setTaskStatus(TaskStatus.FAILED.getValue());
                dto.setUsageTime(usedTime) ;

                generateTaskService.resetRetryTask(dto);
            }else{
                dto.setAssistantContent(sb.toString());
                dto.setUsageTime(usedTime) ;
                dto.setTaskStatus(TaskStatus.COMPLETED.getValue());
                generateTaskService.update(dto);
            }

            // 记录历史记录
            recordHistory(dto , sb.toString()) ;
            log.debug("assistant content: \r\n{}" , dto.getAssistantContent());
        }
    }

    protected void recordHistory(GenerateTaskEntity dto, String genContent) {
        IGenerateTaskHistoryService generateTaskHistoryService = SpringContext.getBean(IGenerateTaskHistoryService.class) ;

        GenerateTaskHistoryEntity generateTaskHistoryEntity = new GenerateTaskHistoryEntity() ;
        BeanUtils.copyProperties(dto , generateTaskHistoryEntity);
        dto.setAssistantContent(genContent);

        generateTaskHistoryService.save(generateTaskHistoryEntity) ;
    }

    @NotNull
    protected List<Message> getMessages(GenerateTaskEntity dto) {

        IGenerateTaskService generateTaskService = SpringContext.getBean(IGenerateTaskService.class) ;

        String promptId = dto.getPromptId() ;
        List<Message> messages = new ArrayList<>() ;

        try{
            parseMessage(dto, promptId, messages);
        }catch(Exception e){
            log.error("模板解析异常:{}" , e.getMessage());
            dto.setTaskStatus(TaskStatus.EXCEPTION.getValue());
            dto.setRetryCount(Integer.MAX_VALUE);
            generateTaskService.update(dto) ;
        }
        Assert.isTrue(!messages.isEmpty() , "消息内容为空，任务不处理:{}");

        return messages;
    }

    protected void parseMessage(GenerateTaskEntity dto, String promptId, List<Message> messages) {
        PromptPostsEntity postsEntity = promptPostsService.getByPromptId(promptId) ;

        List<PromptMessageDto> promptMessageList = JSONArray.parseArray(postsEntity.getPromptContent() , PromptMessageDto.class) ;
        Map<?,?> params = JSONObject.parseObject(dto.getParams() , Map.class) ;


        for(PromptMessageDto msg : promptMessageList){
            Message message = null ;

            // 模板解析处理
            String contentTemplate = msg.getContent().trim() ;
            if(params != null){
                contentTemplate = TemplateParser.parserTemplate(contentTemplate , params) ;
            }

            if(Message.Role.SYSTEM.getValue().equals(msg.getRole())){
                message = Message.ofSystem(contentTemplate);
            }else if(Message.Role.ASSISTANT.getValue().equals(msg.getRole())){
                message = Message.ofAssistant(contentTemplate);
            }else if(Message.Role.USER.getValue().equals(msg.getRole())){
                message = Message.of(contentTemplate);
            }

            if(message != null){
                messages.add(message) ;
            }

        }
    }
}
