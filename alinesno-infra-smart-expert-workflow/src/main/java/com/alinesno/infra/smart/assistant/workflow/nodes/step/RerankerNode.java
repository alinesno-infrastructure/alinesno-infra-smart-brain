// RerankerNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示多路召回节点，继承自 AbstractFlowNode 类。
 * 其作用是使用重排模型对多个知识库的检索结果进行二次召回，
 * 以提高检索结果的准确性和相关性。在工作流中，当需要对初步检索结果进行优化时，会使用该节点。
 */
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "reranker")
@EqualsAndHashCode(callSuper = true)
public class RerankerNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "reranker"。
     */
    public RerankerNode() {
        setType("reranker");
    }
}