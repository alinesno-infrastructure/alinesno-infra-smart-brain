package com.alinesno.infra.smart.assistant.role.llm;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.utils.Constants;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class QianWenLLM {

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String qianWenKey;

    @SneakyThrows
    public StringBuilder chatComponent(MessageManager msgManager) {

        Constants.apiKey = qianWenKey ;
        Generation gen = new Generation();

        QwenParam param = QwenParam.builder()
                        .model(Generation.Models.QWEN_TURBO)
                        .messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
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