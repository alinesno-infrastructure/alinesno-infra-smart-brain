// QuestionNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示问题优化节点，继承自 AbstractFlowNode 类。
 * 主要功能是根据历史聊天记录优化完善当前问题，使问题更利于匹配知识库中的分段内容。
 * 在工作流中，当需要提高问题与知识库匹配度时，会使用该节点。
 */
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
    protected void handleNode() {

    }
}