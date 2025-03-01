// KnowledgeSearchNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 该类表示知识库检索节点，继承自 AbstractFlowNode 类。
 * 主要用于关联知识库，查找与用户提出的问题相关的分段内容。
 * 在工作流中，当需要从知识库中获取相关信息时，会使用该节点。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KnowledgeSearchNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "knowledge_search"。
     */
    public KnowledgeSearchNode() {
        setType("knowledge_search");
    }
}