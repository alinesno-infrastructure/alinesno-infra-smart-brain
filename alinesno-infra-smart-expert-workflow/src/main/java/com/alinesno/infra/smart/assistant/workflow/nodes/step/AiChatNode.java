// AiChatNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 该类表示 AI 对话节点，继承自 AbstractFlowNode 类。
 * 用于在工作流中实现与 AI 大模型进行对话的功能。
 * 当工作流执行到该节点时，会触发与 AI 大模型的交互过程。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "ai_chat"。
     */
    public AiChatNode() {
        setType("ai_chat");
    }
}