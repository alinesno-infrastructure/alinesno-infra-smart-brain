// DocumentExtractNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.DocumentExtractNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示文档内容提取节点，继承自 AbstractFlowNode 类。
 * 用于从文档中提取所需的内容，如文本、数据等。
 * 在工作流中，当需要对文档进行内容解析和提取时，会使用该节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "document_extract")
@EqualsAndHashCode(callSuper = true)
public class DocumentExtractNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "document_extract"。
     */
    public DocumentExtractNode() {
        setType("document_extract");
    }

    @Override
    protected void handleNode() {
        DocumentExtractNodeData nodeData = getNodeData() ;
        log.debug("nodeData = {}" , nodeData) ;
        log.debug("node type = {} output = {}" , node.getType() , output);

        String content = "文档提取内容" ;
        output.put(node.getStepName()+".content" , content);
    }

    private DocumentExtractNodeData getNodeData(){
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data")) ;
        return JSONObject.parseObject(nodeDataJson , DocumentExtractNodeData.class) ;
    }
}