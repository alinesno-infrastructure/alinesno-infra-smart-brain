// TextToSpeechNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.TextToSpeechNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示文本转语音节点，继承自 AbstractFlowNode 类。
 * 主要用于将文本通过语音合成模型转换为音频。
 * 在工作流中，当需要将文本信息以语音形式输出时，会使用该节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "text_to_speech")
@EqualsAndHashCode(callSuper = true)
public class TextToSpeechNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "text_to_speech"。
     */
    public TextToSpeechNode() {
        setType("text_to_speech");
    }

    @Override
    protected void handleNode() {
        TextToSpeechNodeData nodeData = getNodeData() ;
        log.debug("nodeData = {}" , nodeData) ;
        log.debug("node type = {} output = {}" , node.getType() , output);

        String result = "语音地址" ;
        output.put(node.getStepName()+".result" , result);
    }

    private TextToSpeechNodeData getNodeData(){
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data")) ;
        return JSONObject.parseObject(nodeDataJson , TextToSpeechNodeData.class) ;
    }
}