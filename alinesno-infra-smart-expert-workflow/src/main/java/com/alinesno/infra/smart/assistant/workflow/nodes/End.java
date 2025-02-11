// End.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是工作流中的结束节点类，继承自 AbstractWorkflowNode 类。
 * 该节点用于定义一个 workflow 流程结束的最终输出内容。
 * 当工作流执行到最后阶段，此节点会输出整个工作流处理的最终结果。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class End extends AbstractWorkflowNode {
    // 定义一个 workflow 流程结束的最终输出内容。
}