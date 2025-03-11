// QuestionNode.java
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    @SneakyThrows
    @Override
    protected void handleNode() {
        QuestionNodeData nodeData = getAiChatProperties() ;
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
//                CompletableFuture<String> future = getAiChatCompletableFuture(llm , nodeData.getPrompt()) ;
//                CompletableFuture<String> future = getAiChatCompletableFuture(llm , nodeData.getPrompt()) ;
//                String chatResult = future.get() ;

                CompletableFuture<String> future = getAiChatResultAsync(llm, replacePlaceholders(nodeData.getPrompt()));
                // 设置超时时间为 120 秒
                String chatResult = future.get(120, TimeUnit.SECONDS);
                System.out.println("Chat result: " + chatResult);

                output.put(node.getStepName()+".answer" , chatResult);
            }

        }
    }

    private QuestionNodeData getAiChatProperties(){
        String nodeDataJson =  node.getProperties().get("node_data")+"" ;
        return StringUtils.isNotEmpty(nodeDataJson)? JSONObject.parseObject(nodeDataJson , QuestionNodeData.class):null;
    }

}