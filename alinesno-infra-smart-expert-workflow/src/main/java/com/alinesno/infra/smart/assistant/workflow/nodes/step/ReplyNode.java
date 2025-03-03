// ReplyNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示指定回复节点，继承自 AbstractFlowNode 类。
 * 用于指定回复内容，并且引用变量会转换为字符串进行输出。
 * 在工作流中，当需要给出固定的回复信息时，会使用该节点。
 */
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

    @Override
    protected void handleNode() {

    }
}