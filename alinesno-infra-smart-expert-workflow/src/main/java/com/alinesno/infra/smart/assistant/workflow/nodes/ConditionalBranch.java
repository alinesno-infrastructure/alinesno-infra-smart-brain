// ConditionalBranch.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是条件分支节点类，继承自 AbstractWorkflowNode 类。
 * 该节点允许根据 if/else 条件将 workflow 拆分成两个分支。
 * 在工作流执行过程中，当需要根据某些条件来决定后续执行的流程时，会使用此节点进行条件判断并选择相应的分支继续执行。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class ConditionalBranch extends AbstractWorkflowNode {
    // 允许你根据 if/else 条件将 workflow 拆分成两个分支。
}