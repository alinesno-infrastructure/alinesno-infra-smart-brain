package com.alinesno.infra.smart.brain.inference.llm.chatgpt;// FileProcessingServiceImpl.java

import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.inference.llm.FileProcessingParent;
import com.plexpt.chatgpt.entity.chat.ChatChoice;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 任务解析离线任务类
 */
@Slf4j
//@Service
public class ChatGPTProcessing extends FileProcessingParent {

    /**
     * 实现处理文件的方法
     * @param dto 文件路径
     * @return CompletableFuture<String> 异步返回处理结果
     */
    @Override
    public void processFile(GenerateTaskEntity dto) {

        List<Message> messages = getMessages(dto);

        ChatCompletion chatCompletion = getChatCompletion(messages);
        llmApiService.streamChatCompletion(chatCompletion, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {

                boolean isFinish = false ;

                long startTime = System.currentTimeMillis() ;

                StringBuilder sb = new StringBuilder();
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
                            sb.append(text) ;
                            System.out.print(text);
                        }
                    }
                }

                extractedFinishTask(sb , startTime, isFinish , dto);
            }
        });
    }


}