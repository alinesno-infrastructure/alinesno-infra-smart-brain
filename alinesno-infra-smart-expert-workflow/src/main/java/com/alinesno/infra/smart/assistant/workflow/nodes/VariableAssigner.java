// VariableAssigner.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是变量赋值节点类，继承自 AbstractWorkflowNode 类。
 * 该节点用于向可写入变量（例如会话变量）进行变量赋值。
 * 在工作流中，如果需要对某个变量进行赋值操作，会使用此节点将指定的值赋给相应的变量。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class VariableAssigner extends AbstractWorkflowNode {
    // 变量赋值节点用于向可写入变量（例如会话变量）进行变量赋值。
}