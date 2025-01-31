package com.alinesno.infra.smart.assistant.role.llm;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.Constants;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.role.llm.adapter.MessageManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * // TODO 待优化线程池 https://help.aliyun.com/zh/dashscope/java-sdk-best-practices?spm=a2c4g.11186623.0.0.dabb3e25l88IO5
 */
@Slf4j
@Service
public class QianWenLLM {

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String qianWenKey;

    @Value("${alinesno.infra.smart.brain.qianwen.add-protocol:true}")
    private boolean protocol ;

    @SneakyThrows
    public void getGeneration(MessageManager msgManager , ResultCallback<GenerationResult> callback) {

        Constants.apiKey = qianWenKey ;
        Generation gen = new Generation();

        GenerationParam param =GenerationParam.builder()
                .model(Generation.Models.QWEN_TURBO)
                .messages(msgManager.get())
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .incrementalOutput(true)
                .build();

        gen.streamCall(param, callback) ;
    }

    public Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }

    @SneakyThrows
    public void getGeneration(List<Message> messages , ResultCallback<GenerationResult> callback) {

        Constants.apiKey = qianWenKey ;
        Generation gen = new Generation();

        // 添加思考协议
        if(protocol){
            Message thinkingProtocolPrompt = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(ThinkingProtocol.prompt)
                    .build();
            messages.add(0 , thinkingProtocolPrompt);
        }

        GenerationParam param =GenerationParam.builder()
                .model(Generation.Models.QWEN_TURBO)
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .incrementalOutput(true)
                .build();

        gen.streamCall(param, callback) ;
    }

    @SneakyThrows
    public StringBuilder chatComponent(String prompt) {

        Message promptMsg = Message.builder()
                .role("user")
                .content(prompt)
                .build();

        MessageManager msgManager = new MessageManager(10);
        msgManager.add(promptMsg);

        return chatComponent(msgManager) ;
    }

    @SneakyThrows
    public StringBuilder chatComponent(MessageManager msgManager) {

        Constants.apiKey = qianWenKey ;
        Generation gen = new Generation();

        // 添加思考协议
        if(protocol){
            Message thinkingProtocolPrompt = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(ThinkingProtocol.prompt)
                    .build();
            msgManager.addFirst(thinkingProtocolPrompt);
        }

        GenerationParam param = GenerationParam.builder()
                        .model(Generation.Models.QWEN_TURBO)
                        .messages(msgManager.get())
                        .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .enableSearch(true)
                        .build();
        GenerationResult result = gen.call(param);

        String content = result.getOutput().getChoices().get(0).getMessage().getContent() ;
        log.debug("content = {}" , content);

        return new StringBuilder().append(content);
    }

    public String processFile(List<PromptMessage> messages) {

        MessageManager msgManager = new MessageManager(10);

        for(PromptMessage message : messages){
            com.alibaba.dashscope.common.Message msg = com.alibaba.dashscope.common.Message.builder()
                    .role(message.getRole())
                    .content(message.getContent())
                    .build();
            msgManager.add(msg);
        }

        StringBuilder content = chatComponent(msgManager) ;

        return content.toString();
    }


}