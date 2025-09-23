package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.QuestionNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 该类表示问题优化节点，继承自 AbstractFlowNode 类。
 * 主要功能是根据历史聊天记录优化完善当前问题，使问题更利于匹配知识库中的分段内容。
 * 在工作流中，当需要提高问题与知识库匹配度时，会使用该节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "question")
@EqualsAndHashCode(callSuper = true)
public class QuestionNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "question"。
     */
    public QuestionNode() {
        setType("question");
    }

    @Override
    protected CompletableFuture<Void> handleNode() {
        QuestionNodeData nodeData = getAiChatProperties();
        log.debug("node type = {}" , nodeData);

        if (nodeData == null) {
            return CompletableFuture.completedFuture(null);
        }

        String llmModelId = nodeData.getLlmModelId();
        LlmModelEntity llmModel = llmModelService.getById(llmModelId);

        log.debug("llmModel = {}" , llmModel);

        if (llmModel != null && "qwen".equals(llmModel.getProviderCode())) {
            QwenLlmConfig config = new QwenLlmConfig();
            config.setEndpoint(llmModel.getApiUrl());
            config.setApiKey(llmModel.getApiKey());
            config.setModel(llmModel.getModel());

            Llm llm = new QwenLlm(config);

            CompletableFuture<String> future = getAiChatResultAsync(llm, replacePlaceholders(nodeData.getPrompt()));
            return future.thenAccept(chatResult -> {
                log.debug("Chat result: {}", chatResult);
                output.put(node.getStepName() + ".answer", chatResult);
            }).exceptionally(ex -> {
                log.error("QuestionNode 执行异常: {}", ex.getMessage(), ex);
                CompletableFuture<Void> failed = new CompletableFuture<>();
                failed.completeExceptionally(ex);
                return null;
            });
        }

        return CompletableFuture.completedFuture(null);
    }

    private QuestionNodeData getAiChatProperties(){
        String nodeDataJson =  node.getProperties().get("node_data")+"" ;
        return StringUtils.isNotEmpty(nodeDataJson)? JSONObject.parseObject(nodeDataJson , QuestionNodeData.class):null;
    }

}