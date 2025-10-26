package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.message.Message;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.agentsflex.core.prompt.template.TextPromptTemplate;
import com.alinesno.infra.smart.assistant.api.config.ContextEngineeringData;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepSearchContext;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt.WorkerPrompt;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
import com.alinesno.infra.smart.utils.TokenUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 上下文工具，规避超过上下文 <br/>
 * - LoopDetectionService (loopDetectionService.ts) - 循环检测
 * - tryCompressChat 聊天历史压缩机制
 */
@Component
public class DsContextEngineerUtils {

    // 压缩触发条件和参数
    private static final double COMPRESSION_TOKEN_THRESHOLD = 0.7;  // 70% token 使用率触发压缩
    private static final double COMPRESSION_PRESERVE_THRESHOLD = 0.3; // 保留 30% 最新历史
    private static final int MAX_TURNS = 100; // 最大会话轮次

    /**
     * 尝试压缩聊天历史
     *
     * @param historyPrompt
     * @param toolOutput
     * @param roleMaxHistory
     * @param contextEngineeringData
     * @param step
     * @return
     */
    public CompletableFuture<String> tryCompressChat(DeepSearchContext context,
                                                     String goal ,
                                                     HistoriesPrompt historyPrompt,
                                                     StringBuilder toolOutput ,
                                                     String datasetKnowledgeDocument ,
                                                     int roleMaxHistory ,
                                                     ContextEngineeringData contextEngineeringData,
                                                     DeepSearchFlow.Step step) {

        // 判断prompt是否已经超出Token长度

        int originalTokenCount = 0 ;

        List<Message> messages =  historyPrompt.getMemory().getMessages() ;

        if(!CollectionUtils.isEmpty(messages)){
            for(Message message : messages){
                originalTokenCount += TokenUtils.estimateTokenCount(message.getMessageContent().toString());
            }
        }

        originalTokenCount += TokenUtils.estimateTokenCount(toolOutput + datasetKnowledgeDocument);

        // 检查是否需要压缩
        if(originalTokenCount > COMPRESSION_TOKEN_THRESHOLD * contextEngineeringData.getMaxContextLength()){

            DeepSearchFlow.StepAction stepActionDto = getStepAction(contextEngineeringData, originalTokenCount);
            context.getRecordManager().markTaskWorkerSingleStep(stepActionDto, StepActionStatusEnums.DONE.getKey() , context.getSessionId());
            step.addAction(stepActionDto);

            // 更新 flow step 并发布事件
            context.getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
            context.getDeepSearchFlow().getSteps().add(step);
            context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());

            String compressPrompt = "" ;

            String llmModelId =  contextEngineeringData.getLlmModel() ;

            LlmModelEntity llmModel = context.getLlmModelService().getById(llmModelId) ;
            Assert.notNull(llmModel, "模型未配置或者不存在.");

            LlmConfig config = new LlmConfig() ;

            config.setEndpoint(llmModel.getApiUrl());
            config.setApiKey(llmModel.getApiKey()) ;
            config.setModel(llmModel.getModel()) ;

            Llm llm =  context.getLlmAdapter().getLlm(llmModel.getProviderCode(), config);

            String contextSummeryPromptTemplate = contextEngineeringData.getSummaryPrompt() ;

            Map<String, Object> map = new HashMap<>();
            map.put("goal", goal);
            map.put("toolObs", toolOutput);
            map.put("knowledge", datasetKnowledgeDocument);
            TextPromptTemplate promptTemplate = TextPromptTemplate.of(contextSummeryPromptTemplate) ;

            String historyTextPrompt = promptTemplate.format(map).toString();

            String stepMessage = "附件上下文长度超出指定的长度，将内容进行压缩.";
            DeepSearchFlow.StepAction stepCompressActionDto = new DeepSearchFlow.StepAction();
            stepCompressActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
            stepCompressActionDto.setActionType(StepActionEnums.CONTEXT_ENGINEERING.getActionType());
            stepCompressActionDto.setStatus(StepActionStatusEnums.DOING.getKey());
            stepCompressActionDto.setResult(stepMessage);
            stepCompressActionDto.setActionName(stepCompressActionDto.getActionName() + "|" + stepMessage);
            step.addAction(stepCompressActionDto);

            // 更新 flow step 并发布事件
            context.getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
            context.getDeepSearchFlow().getSteps().add(step);
            context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());

            compressPrompt = llm.chat(historyTextPrompt) ;

            System.out.println("内容压缩:" + compressPrompt);
            stepCompressActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
            stepCompressActionDto.setResult(compressPrompt) ;
            context.getRecordManager().markTaskWorkerSingleStep(stepCompressActionDto, StepActionStatusEnums.DONE.getKey() , context.getSessionId());

            // 组合成新的对话内容
            toolOutput.setLength(0) ;
            historyPrompt = new HistoriesPrompt();
            historyPrompt.setMaxAttachedMessageCount(roleMaxHistory);
            historyPrompt.setHistoryMessageTruncateEnable(true);
            historyPrompt.addMessage(new SystemMessage(context.getDeepSearchPromptData().getWorkerPrompt() + WorkerPrompt.DEFAULT_SYSTEM_WORKER_PROMPT));

            return CompletableFuture.completedFuture(compressPrompt);
        }else{
            return CompletableFuture.completedFuture(datasetKnowledgeDocument);
        }
    }

    @NotNull
    private static DeepSearchFlow.StepAction getStepAction(ContextEngineeringData contextEngineeringData, int originalTokenCount) {
        String stepMessage = "当前上下文长度("+ originalTokenCount +":"+(COMPRESSION_TOKEN_THRESHOLD * contextEngineeringData.getMaxContextLength())+")超出指定的长度，将内容进行压缩." ; // , AgentConstants.STEP_START, processChatId, taskInfo);

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
        stepActionDto.setActionType(StepActionEnums.CONTEXT_ENGINEERING.getActionType());
        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
        stepActionDto.setActionName(stepActionDto.getActionName() + "|" + stepMessage);
        stepActionDto.setResult(stepMessage);
        return stepActionDto;
    }

}
