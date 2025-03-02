// ReplyNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 开始节点类，继承自 AbstractFlowNode。
 */
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "start")
@EqualsAndHashCode(callSuper = true)
public class StartNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "start"。
     */
    public StartNode() {
        setType("start");
    }
}