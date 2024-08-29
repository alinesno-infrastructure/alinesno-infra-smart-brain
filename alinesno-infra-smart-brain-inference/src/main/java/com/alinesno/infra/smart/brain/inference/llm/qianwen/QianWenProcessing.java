package com.alinesno.infra.smart.brain.inference.llm.qianwen;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.utils.Constants;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.inference.llm.FileProcessingParent;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class QianWenProcessing extends FileProcessingParent {

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String qianWenKey;

    @SneakyThrows
    public StringBuilder chatComponent(MessageManager msgManager) {

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

        return new StringBuilder().append(content);
    }

    @Async
    @Override
    public void processFile(GenerateTaskEntity dto) {

        log.debug("任务已提交，在执行中:{}", dto.getBusinessId());
        List<Message> messages = getMessages(dto);

        MessageManager msgManager = new MessageManager(10);
        for(Message message : messages){
            com.alibaba.dashscope.common.Message msg = com.alibaba.dashscope.common.Message.builder().role(message.getRole()).content(message.getContent()).build();
            msgManager.add(msg);
        }

        long startTime = System.currentTimeMillis() ;
        StringBuilder content = chatComponent(msgManager) ;

        extractedFinishTask(content, startTime, true, dto);

    }
}