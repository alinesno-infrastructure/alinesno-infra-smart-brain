// ParameterExtractor.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是参数提取器节点类，继承自 AbstractWorkflowNode 类。
 * 该节点利用大语言模型（LLM）从自然语言推理并提取结构化参数，提取的参数可用于后置的工具调用或 HTTP 请求。
 * 在工作流中，当需要从用户的自然语言输入中提取出结构化的参数以便后续使用时，会使用此节点完成参数提取任务。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class ParameterExtractor extends AbstractWorkflowNode {
    // 利用 LLM 从自然语言推理并提取结构化参数，用于后置的工具调用或 HTTP 请求。
}