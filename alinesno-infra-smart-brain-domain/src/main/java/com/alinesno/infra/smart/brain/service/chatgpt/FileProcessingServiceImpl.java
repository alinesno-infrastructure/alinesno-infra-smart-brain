package com.alinesno.infra.smart.brain.service.chatgpt;// FileProcessingServiceImpl.java
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.service.IFileProcessingService;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.listener.AbstractStreamListener;
import com.unfbx.chatgpt.constant.OpenAIConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

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
    private String apiHost = OpenAIConst.OPENAI_HOST;

    private static ChatGPTStream chatGPTStream ;

    /**
     * 实现处理文件的方法
     * @param dto 文件路径
     * @return CompletableFuture<String> 异步返回处理结果
     */
    @Override
    @Async
    public CompletableFuture<String> processFile(BrainTaskDto dto) {

        processFile(dto.getBusinessId() , dto.getSystemContent() , dto.getUserContent()) ;

        return null ;
    }

    private void processFile(String businessId , String systemMessage , String messageContent){

        if(chatGPTStream == null){
            chatGPTStream = ChatGPTStream.builder()
                    .timeout(600)
                    .apiKey(apiKey)
                    .apiHost(apiHost)
                    .build()
                    .init();
        }

        Message system = Message.ofSystem(systemMessage.trim());
        Message message = Message.of(messageContent.trim());

        AbstractStreamListener listener = new AbstractStreamListener() {

            StringBuffer chatStringBuffer = new StringBuffer() ;

            @Override
            public void onMsg(String s) {
                chatStringBuffer.append(s) ;
                System.out.print(s) ;
            }

            @Override
            public void onError(Throwable throwable, String s) {

            }
        };

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_4.getName())
                .messages(Arrays.asList(system, message))
                .temperature(0.5)
                .build();

        chatGPTStream.streamChatCompletion(chatCompletion, listener);
    }

}