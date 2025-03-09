// ReplyNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.HttpApiNodeData;
import com.alinesno.infra.smart.assistant.workflow.utils.HttpApiRequestor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * HttpApiNode类，继承自AbstractFlowNode，表示一个HTTP API节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "http_api")
@EqualsAndHashCode(callSuper = true)
public class HttpApiNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "reply"。
     */
    public HttpApiNode() {
        setType("http_api");
    }

    @Override
    protected void handleNode() {
        HttpApiNodeData nodeData = getNodeData() ;
        log.debug("nodeData = {}" , nodeData) ;
        log.debug("node type = {} output = {}" , node.getType() , output);

        String result = HttpApiRequestor.sendRequest(nodeData) ;
        eventNodeMessage(result) ;

    }

    private HttpApiNodeData getNodeData(){
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data")) ;
        return JSONObject.parseObject(nodeDataJson , HttpApiNodeData.class) ;
    }
}