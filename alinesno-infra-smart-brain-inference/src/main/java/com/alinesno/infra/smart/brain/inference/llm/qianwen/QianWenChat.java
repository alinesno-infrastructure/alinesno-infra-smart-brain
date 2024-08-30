package com.alinesno.infra.smart.brain.inference.llm.qianwen;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.utils.Constants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QianWenChat {

    public static final int MAX_TOKENS = 3096;

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String qianWenKey;

    @SneakyThrows
    public String chatComponent(MessageManager msgManager) {

        Constants.apiKey = qianWenKey ;
        Generation gen = new Generation();

        QwenParam param =
                QwenParam.builder().model(Generation.Models.QWEN_TURBO).messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .enableSearch(true)
                        .build();
        GenerationResult result = gen.call(param);

        String content = result.getOutput().getChoices().get(0).getMessage().getContent() ;
        log.debug("content = {}" , content);

        return content ;
    }

}