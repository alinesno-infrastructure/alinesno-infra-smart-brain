// ReplyNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.ReplayNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示指定回复节点，继承自 AbstractFlowNode 类。
 * 用于指定回复内容，并且引用变量会转换为字符串进行输出。
 * 在工作流中，当需要给出固定的回复信息时，会使用该节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "reply")
@EqualsAndHashCode(callSuper = true)
public class ReplyNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "reply"。
     */
    public ReplyNode() {
        setType("reply");
    }

//    @SneakyThrows
//    @Override
//    protected void handleNode() {
//        eventMessage("指定回复内容.");
//    }

    @Override
    protected void handleNode() {
        ReplayNodeData nodeData = getNodeData() ;
        log.debug("nodeData = {}" , nodeData) ;
        log.debug("node type = {} output = {}" , node.getType() , output);

        String answer = nodeData.getReplayContent() ;

        if(nodeData.getReplayType().equals("text")){  // 直接返回回复的内容
            answer = replacePlaceholders(answer);
            eventNodeMessage(answer) ;
        }

        output.put(node.getStepName()+".answer" , answer);
    }

    private ReplayNodeData getNodeData(){
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data")) ;
        return JSONObject.parseObject(nodeDataJson , ReplayNodeData.class) ;
    }
}