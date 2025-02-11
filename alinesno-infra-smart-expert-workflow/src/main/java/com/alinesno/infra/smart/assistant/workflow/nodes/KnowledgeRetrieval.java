// KnowledgeRetrieval.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是知识检索节点类，继承自 AbstractWorkflowNode 类。
 * 该节点用于从知识库中检索与用户问题相关的文本内容，检索到的内容可作为下游 LLM 节点的上下文。
 * 在工作流中，当需要借助知识库的信息来辅助大语言模型进行回答时，会使用此节点进行知识检索。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class KnowledgeRetrieval extends AbstractWorkflowNode {
    // 从知识库中检索与用户问题相关的文本内容，可作为下游 LLM 节点的上下文。
}