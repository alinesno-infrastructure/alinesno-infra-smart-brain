// LLM.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是大语言模型节点类，继承自 AbstractWorkflowNode 类。
 * 该节点的主要功能是调用大语言模型回答问题或者对自然语言进行处理。
 * 在工作流中，如果需要进行自然语言处理、文本生成、问答等操作，会使用此节点调用大语言模型完成相应任务。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class LLM extends AbstractWorkflowNode {
    // 调用大语言模型回答问题或者对自然语言进行处理。
}