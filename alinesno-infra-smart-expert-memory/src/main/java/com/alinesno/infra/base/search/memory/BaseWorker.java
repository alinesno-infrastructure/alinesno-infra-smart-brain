package com.alinesno.infra.base.search.memory;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class BaseWorker {

    @Value("${alinesno.base.search.dashscope.api-key:}")
    protected String apiKey; // 注意：这个API密钥看起来与数据库配置无关，你可能需要根据实际情况处理它

    public List<Message> promptToMsg(String systemPrompt, String fewShot, String userQuery) {
        return promptToMsg(systemPrompt, fewShot, userQuery, true);
    }

    public List<Message> promptToMsg(String systemPrompt, String fewShot, String userQuery, boolean concatSystemPrompt) {
        String systemContent = systemPrompt.trim();
        String userContent = systemContent + "\r\n" + fewShot + "\r\n" +  userQuery ;

        Message systemMessage = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemContent)
                .build();

        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(userContent)
                .build() ;

        return new ArrayList<>(concatSystemPrompt?List.of(systemMessage, userMessage):List.of(userMessage));
    }

    protected String getResultContent(String systemPrompt, String fewShot, String userQuery) throws NoApiKeyException, InputRequiredException {
        List<Message> messages = promptToMsg(systemPrompt, fewShot, userQuery);
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model(Generation.Models.QWEN_TURBO)
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        Generation gen = new Generation();
        GenerationResult result = gen.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }

}
