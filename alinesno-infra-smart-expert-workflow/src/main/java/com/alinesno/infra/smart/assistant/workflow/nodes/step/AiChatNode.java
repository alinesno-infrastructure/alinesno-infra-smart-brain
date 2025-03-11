// AiChatNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.role.context.AgentConstants;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.AiChatNodeData;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 该类表示 AI 对话节点，继承自 AbstractFlowNode 类。
 * 用于在工作流中实现与 AI 大模型进行对话的功能。
 * 当工作流执行到该节点时，会触发与 AI 大模型的交互过程。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "ai_chat")
@EqualsAndHashCode(callSuper = true)
public class AiChatNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "ai_chat"。
     */
    public AiChatNode() {
        setType("ai_chat");
    }

    @SneakyThrows
    @Override
    protected void handleNode() {
        AiChatNodeData nodeData = getAiChatProperties() ;
        log.debug("node type = {}" , nodeData) ;

        if(nodeData != null){
            String llmModelId = nodeData.getLlmModelId() ;
            LlmModelEntity llmModel = llmModelService.getById(llmModelId) ;

            log.debug("llmModel = {}" , llmModel)  ;

            if(llmModel != null && llmModel.getProviderCode().equals("qwen")){
                QwenLlmConfig config = new QwenLlmConfig();
                config.setEndpoint(llmModel.getApiUrl()); ;
                config.setApiKey(llmModel.getApiKey()) ;
                config.setModel(llmModel.getModel()) ;

                Llm llm = new QwenLlm(config);

                CompletableFuture<String> future = getAiChatResultAsync(llm, replacePlaceholders(nodeData.getPrompt()));
                // 设置超时时间为 60 秒
                String chatResult = future.get(120, TimeUnit.SECONDS);
                System.out.println("Chat result: " + chatResult);

                output.put(node.getStepName()+".answer", chatResult) ;
                output.put(node.getStepName()+".reasoning_content", chatResult) ;
            }

        }
    }

    private AiChatNodeData getAiChatProperties(){
        String nodeDataJson =  node.getProperties().get("node_data")+"" ;
        return StringUtils.isNotEmpty(nodeDataJson)? JSONObject.parseObject(nodeDataJson , AiChatNodeData.class):null;
    }

    @NotNull
    protected CompletableFuture<String> getAiChatCompletableFuture(Llm llm , String prompt) {
        return CompletableFuture.supplyAsync(() -> {

            AtomicReference<String> outputStr = new AtomicReference<>("");

            llm.chatStream(prompt , (context, response) -> {
                AiMessage message = response.getMessage();
                System.out.println(">>>> " + message);

                FlowStepStatusDto stepDto = new FlowStepStatusDto();

                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(node.getId());
                stepDto.setStatus(AgentConstants.STEP_PROCESS);
                stepDto.setFlowChatText(message.getContent());
                stepDto.setPrint(node.isPrint());

                taskInfo.setFlowStep(stepDto);


                if(message.getStatus() == MessageStatus.END){
                    outputStr.set(message.getFullContent());

                    stepDto.setStatus(AgentConstants.STEP_FINISH);
                    streamMessagePublisher.doStuffAndPublishAnEvent(null,
                            role,
                            taskInfo,
                            taskInfo.getTraceBusId());
                }else{
                    streamMessagePublisher.doStuffAndPublishAnEvent(null,
                            role,
                            taskInfo,
                            taskInfo.getTraceBusId());
                }

            });

            // 生成任务结果
            return outputStr.toString() ;
        });
    }

}