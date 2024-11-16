package com.alinesno.infra.smart.assistant.role.llm;

import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 千问新的API接口封装，用于ReAct特定模式
 */
@Slf4j
public class QianWenNewApiLLM {

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String qianWenKey;

    public GenerationParam createGenerationParam(List<Message> messages) {
        return GenerationParam.builder()
                .apiKey(qianWenKey)
                .model(Generation.Models.QWEN_TURBO)
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .build();
    }

    public GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

    @SneakyThrows
    public GenerationResult chat(List<Message> messages){

        log.debug("messages:\r\n{}" , JSONUtil.toJsonPrettyStr(messages));

        GenerationParam param = createGenerationParam(messages);

        return callGenerationWithMessages(param);
    }

    public Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }
}