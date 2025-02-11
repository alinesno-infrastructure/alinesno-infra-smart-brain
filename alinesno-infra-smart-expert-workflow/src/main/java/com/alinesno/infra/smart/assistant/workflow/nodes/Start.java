// Start.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是工作流中的开始节点类，继承自 AbstractWorkflowNode 类。
 * 该节点用于定义一个 workflow 流程启动的初始参数。
 * 在工作流开始执行时，此节点负责提供必要的初始信息，这些信息将作为后续节点执行的基础。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class Start extends AbstractWorkflowNode {
    // 定义一个 workflow 流程启动的初始参数。
}