package com.alinesno.infra.smart.brain.inference.llm.chatgpt;// FileProcessingServiceImpl.java

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.entity.GenerateTaskHistoryEntity;
import com.alinesno.infra.smart.brain.entity.PromptPostsEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.inference.event.ChatEventPublisher;
import com.alinesno.infra.smart.brain.inference.tools.TemplateParser;
import com.alinesno.infra.smart.brain.service.IFileProcessingService;
import com.alinesno.infra.smart.brain.service.IGenerateTaskHistoryService;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import com.alinesno.infra.smart.brain.service.IPromptPostsService;
import com.alinesno.infra.smart.brain.utils.CodeBlockParser;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatChoice;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件处理服务接口的实现类
 */
@Slf4j
@Service
public class FileProcessingServiceImpl implements IFileProcessingService {

    @Value("${alinesno.infra.smart.brain.openapi.key:}")
    private String apiKey;

    /**
     * 自定义api host使用builder的方式构造client
     */
    @Value("${alinesno.infra.smart.brain.openapi.host}")
    private String apiHost ;

    private static ChatGPTStream chatGPTStream ;

    @Autowired
    private ChatEventPublisher chatEventPublisher ;

    @Autowired
    private IPromptPostsService promptPostsService ;

    /**
     * 实现处理文件的方法
     * @param dto 文件路径
     * @return CompletableFuture<String> 异步返回处理结果
     */
    @Override
    @Async
    public void processFile(GenerateTaskEntity dto) {

        initClient() ;

        IGenerateTaskService generateTaskService = SpringContext.getBean(IGenerateTaskService.class) ;
        List<Message> messages = getMessages(dto , generateTaskService);

        if(messages.isEmpty()){
            log.warn("消息内容为空，任务不处理:{}" , dto);
           return ;
        }

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .messages(messages)
                .temperature(0.5)
                .build();

        chatGPTStream.streamChatCompletion(chatCompletion, new EventSourceListener(){

            private boolean isFinish = false ;
            private final StringBuilder stringBuilder = new StringBuilder();

            final StopWatch stopWatch = StopWatch.createStarted();

            public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {

                if (data.equals("[DONE]")) {
                    isFinish = true ;
                    log.debug("任务{}输出结束:{}" , dto.getId() , isFinish);
                } else {
                    ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                    List<ChatChoice> choices = response.getChoices();
                    if (choices != null && !choices.isEmpty()) {
                        Message delta = choices.get(0).getDelta();
                        String text = delta.getContent();
                        if (text != null) {
                            stringBuilder.append(text) ;
                            System.out.print(text);
                        }

                    }
                }

                if(isFinish){

                    stopWatch.stop();
                    System.out.println("方法执行时间：" + stopWatch.getTime() / 1000.0 + " 秒");

                    String genContent = stringBuilder.toString() ;

                    // 判断是否获取到代码内容(这里返回的结果里面一定要有代码)
                    List<TaskContentDto.CodeContent> codeContents =  CodeBlockParser.parseCodeBlocks(genContent) ;

                    if(codeContents.isEmpty()){ // 未完成，则重新生成内容

                        dto.setAssistantContent(genContent);
                        dto.setTaskStatus(TaskStatus.FAILED.getValue());
                        dto.setUsageTime((long) (stopWatch.getTime() / 1000.0));

                        generateTaskService.resetRetryTask(dto);

                    }else{
                        dto.setAssistantContent(genContent);
                        dto.setUsageTime((long) (stopWatch.getTime() / 1000.0));
                        dto.setTaskStatus(TaskStatus.COMPLETED.getValue());
                        generateTaskService.update(dto);
                    }

                    // 记录历史记录
                    recordHistory(dto , genContent) ;

                    log.debug("assistant content: \r\n{}" , dto.getAssistantContent());
                }
            }

        });
    }

    private void recordHistory(GenerateTaskEntity dto, String genContent) {
        IGenerateTaskHistoryService generateTaskHistoryService = SpringContext.getBean(IGenerateTaskHistoryService.class) ;

        GenerateTaskHistoryEntity generateTaskHistoryEntity = new GenerateTaskHistoryEntity() ;
        BeanUtils.copyProperties(dto , generateTaskHistoryEntity);
        dto.setAssistantContent(genContent);

        generateTaskHistoryService.save(generateTaskHistoryEntity) ;
    }

    @NotNull
    private List<Message> getMessages(GenerateTaskEntity dto , IGenerateTaskService generateTaskService) {

        String promptId = dto.getPromptId() ;
        log.debug("promptId = {}" , promptId);
        List<Message> messages = new ArrayList<>() ;

        try{
            parseMessage(dto, promptId, messages);
        }catch(Exception e){
            log.error("模板解析异常:{}" , e.getMessage());
            dto.setTaskStatus(TaskStatus.EXCEPTION.getValue());
            dto.setRetryCount(Integer.MAX_VALUE);
            generateTaskService.update(dto) ;
        }

        log.debug("message = {}", messages);

        return messages;
    }

    private void parseMessage(GenerateTaskEntity dto, String promptId, List<Message> messages) {
        PromptPostsEntity postsEntity = promptPostsService.getByPromptId(promptId) ;
        List<PromptMessageDto> promptMessageList = JSONArray.parseArray(postsEntity.getPromptContent() , PromptMessageDto.class) ;
        Map<String , Object> params = JSONObject.parseObject(dto.getParams() , Map.class) ;


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

    private void initClient(){
        if(chatGPTStream == null){
            chatGPTStream = ChatGPTStream.builder()
                    .timeout(4500)
                    .apiKey(apiKey)
                    .apiHost(apiHost)
                    .build()
                    .init();
        }
    }

}