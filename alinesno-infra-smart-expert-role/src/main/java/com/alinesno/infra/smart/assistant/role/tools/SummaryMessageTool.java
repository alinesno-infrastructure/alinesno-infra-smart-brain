package com.alinesno.infra.smart.assistant.role.tools;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class SummaryMessageTool {

    @SneakyThrows
    public String getSummaryAnswer(Llm llm,
                                   String summaryPrompt,
                                   String summaryChatId ,
                                   MessageTaskInfo taskInfo ,
                                   IndustryRoleEntity role ,
                                   StreamMessagePublisher streamMessagePublisher) {

        CompletableFuture<String> future = getSingleAiChatResultAsync(llm, summaryPrompt , taskInfo , summaryChatId , role , streamMessagePublisher) ;

        String output = future.get();
        log.debug("output = {}" , output) ;

        return output ;
    }

    public CompletableFuture<String> getSingleAiChatResultAsync(Llm llm,
                                                                String prompt,
                                                                MessageTaskInfo taskInfo,
                                                                String summaryChatId ,
                                                                IndustryRoleEntity role,
                                                                StreamMessagePublisher streamMessagePublisher) {

        CompletableFuture<String> future = new CompletableFuture<>();
        final MessageTaskInfo localTaskInfo = taskInfo;
        AtomicReference<String> outputStr = new AtomicReference<>("");

        try {
            llm.chatStream(prompt, (context, response) -> {

                AiMessage message = response.getMessage();

                FlowStepStatusDto stepDto = new FlowStepStatusDto();
                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(summaryChatId);
                stepDto.setStatus(AgentConstants.STEP_PROCESS);

                if(org.springframework.util.StringUtils.hasLength(message.getContent())) {
                    stepDto.setFlowChatText(message.getContent());
                }

                if(StringUtils.hasLength(message.getReasoningContent())){
                    stepDto.setFlowReasoningText(message.getReasoningContent());
                }

                stepDto.setPrint(true);

                synchronized (localTaskInfo) {
                    localTaskInfo.setFlowStep(stepDto);
                }

                try {
                    boolean isEnd = false;
                    synchronized (localTaskInfo) {
                        if (message.getStatus() == MessageStatus.END) {
                            outputStr.set(message.getFullContent());
                            stepDto.setStatus(AgentConstants.STEP_FINISH);
                            isEnd = true;
                        }
                    }

                    streamMessagePublisher.doStuffAndPublishAnEvent(null, role, localTaskInfo, localTaskInfo.getTraceBusId());

                    if (isEnd) {
                        future.complete(outputStr.get());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }


}
