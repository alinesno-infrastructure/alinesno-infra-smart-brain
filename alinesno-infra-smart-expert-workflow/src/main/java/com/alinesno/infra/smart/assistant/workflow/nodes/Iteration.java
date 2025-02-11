// Iteration.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是迭代节点类，继承自 AbstractWorkflowNode 类。
 * 该节点用于对列表对象执行多次步骤直至输出所有结果。
 * 在工作流中，如果需要对一个列表中的每个元素依次执行相同的操作，会使用此节点进行迭代处理。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class Iteration extends AbstractWorkflowNode {
    // 对列表对象执行多次步骤直至输出所有结果。
}