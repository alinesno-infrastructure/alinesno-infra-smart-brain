package com.alinesno.infra.smart.assistant.role.context;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.message.Message;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.agentsflex.core.prompt.template.TextPromptTemplate;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.config.ContextEngineeringData;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.role.prompt.PromptTemplate;
import com.alinesno.infra.smart.assistant.role.utils.TokenUtils;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
public class ContextEngineerUtils {

    // 压缩触发条件和参数
    private static final double COMPRESSION_TOKEN_THRESHOLD = 0.7;  // 70% token 使用率触发压缩
    private static final double COMPRESSION_PRESERVE_THRESHOLD = 0.3; // 保留 30% 最新历史
    private static final int MAX_TURNS = 100; // 最大会话轮次

    @Autowired
    private ILLmAdapterService llmAdapter ;

    private ContextEngineeringData contextEngineeringData ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    /**
     * 尝试压缩聊天历史
     * @param historyPrompt
     * @param toolOutput
     * @param roleMaxHistory
     * @param contextEngineeringData
     * @return
     */
    public CompletableFuture<String> tryCompressChat(MessageTaskInfo taskInfo ,
                                                     String goal ,
                                                     HistoriesPrompt historyPrompt,
                                                     StringBuilder toolOutput ,
                                                     String datasetKnowledgeDocument ,
                                                     int roleMaxHistory ,
                                                     ContextEngineeringData contextEngineeringData) {

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

            String processChatId = IdUtil.getSnowflakeNextIdStr();
            eventStepMessage("当前上下文长度("+originalTokenCount+":"+(COMPRESSION_TOKEN_THRESHOLD * contextEngineeringData.getMaxContextLength())+")超出指定的长度，将内容进行压缩.", AgentConstants.STEP_START, processChatId, taskInfo);

            String compressPrompt = "" ;

            String llmModelId =  contextEngineeringData.getLlmModel() ;

            LlmModelEntity llmModel = llmModelService.getById(llmModelId) ;
            Assert.notNull(llmModel, "模型未配置或者不存在.");

            LlmConfig config = new LlmConfig() ;

            config.setEndpoint(llmModel.getApiUrl());
            config.setApiKey(llmModel.getApiKey()) ;
            config.setModel(llmModel.getModel()) ;

            Llm llm =  llmAdapter.getLlm(llmModel.getProviderCode(), config);

            String contextSummeryPromptTemplate = contextEngineeringData.getSummaryPrompt() ;

            Map<String, Object> map = new HashMap<>();
            map.put("goal", goal);
            map.put("toolObs", toolOutput);
            map.put("knowledge", datasetKnowledgeDocument);
            TextPromptTemplate promptTemplate = TextPromptTemplate.of(contextSummeryPromptTemplate) ;

            String historyTextPrompt = promptTemplate.format(map).toString();
            compressPrompt = llm.chat(historyTextPrompt) ;

            System.out.println("内容压缩:" + compressPrompt);

            eventStepMessage("附件上下文长度超出指定的长度，将内容进行压缩.", AgentConstants.STEP_FINISH, processChatId, taskInfo);

            // 组合成新的对话内容
            toolOutput.setLength(0) ;
            historyPrompt = new HistoriesPrompt();
            historyPrompt.setMaxAttachedMessageCount(roleMaxHistory);
            historyPrompt.setHistoryMessageTruncateEnable(true);
            historyPrompt.addMessage(new SystemMessage(PromptTemplate.REACT_PROMPT_SYSTEM_TEMPLATE));
            return CompletableFuture.completedFuture(compressPrompt);
        }else{
            return CompletableFuture.completedFuture(datasetKnowledgeDocument);
        }
    }

    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo) {
        eventStepMessage(stepMessage, status, stepId, taskInfo, null);
    }

    /**
     * 流程节点消息
     * @param stepMessage
     * @param status
     */
    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo , String stepContent) {

        // stepMessage的最大字符长度不能超过50个字，超过则截取并显示省略号
        if(stepMessage.length() > 50){
            stepMessage = stepMessage.substring(0, 50) + "...";
        }

        FlowStepStatusDto stepDto = new FlowStepStatusDto() ;
        stepDto.setMessage(stepMessage) ;
        stepDto.setStepId(stepId) ;
        stepDto.setStatus(status);
        stepDto.setPrint(false);

        if(StringUtils.hasLength(stepContent)){
            // 如果stepContent的内容超过2048个字符，则进行截取
            if(stepContent.length() > 2048){
                stepContent = stepContent.substring(0, 2048);
            }
            stepDto.setFlowChatText(stepContent);
        }

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null,
                taskInfo.getRoleDto(),
                taskInfo,
                taskInfo.getTraceBusId()
        );

    }
}
