// ConditionNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示判断器节点，继承自 AbstractFlowNode 类。
 * 其功能是根据不同的条件执行不同的节点，实现工作流的分支逻辑。
 * 在工作流中，当需要根据某些条件来决定后续执行的节点时，会使用该节点。
 */
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "condition")
@EqualsAndHashCode(callSuper = true)
public class ConditionNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "condition"。
     */
    public ConditionNode() {
        setType("condition");
    }
}