package com.alinesno.infra.smart.brain.service.chatgpt;// FileProcessingServiceImpl.java

import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.event.ChatEventPublisher;
import com.alinesno.infra.smart.brain.event.TaskEvent;
import com.alinesno.infra.smart.brain.service.IFileProcessingService;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatChoice;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 文件处理服务接口的实现类
 */
@Service
public class FileProcessingServiceImpl implements IFileProcessingService {

    private static final Logger log = LoggerFactory.getLogger(FileProcessingServiceImpl.class) ;

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

    /**
     * 实现处理文件的方法
     * @param dto 文件路径
     * @return CompletableFuture<String> 异步返回处理结果
     */
    @Override
    @Async
    public void processFile(GenerateTaskEntity dto) {

        String systemMessage = dto.getSystemContent() ;
        String userMessage = dto.getUserContent() ;

        if(chatGPTStream == null){
            chatGPTStream = ChatGPTStream.builder()
                    .timeout(600)
                    .apiKey(apiKey)
                    .apiHost(apiHost)
                    .build()
                    .init();
        }

        Message system = Message.ofSystem(systemMessage.trim());
        Message message = Message.of(userMessage.trim());

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_4.getName())
                .messages(Arrays.asList(system, message))
                .temperature(0.5)
                .build();

        chatGPTStream.streamChatCompletion(chatCompletion, new EventSourceListener(){

            private boolean isFinish = false ;
            private final StringBuilder stringBuilder = new StringBuilder();

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
                    dto.setAssistantContent(stringBuilder.toString());

                    TaskEvent taskEvent = new TaskEvent(dto)  ;
                    taskEvent.setId(dto.getId());
                    taskEvent.setAssistantContent(stringBuilder.toString());

                    chatEventPublisher.publishEvent(taskEvent);
                }
            }
        });
    }

}