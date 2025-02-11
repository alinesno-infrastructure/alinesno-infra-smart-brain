// VariableAggregator.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是变量聚合节点类，继承自 AbstractWorkflowNode 类。
 * 该节点用于将多路分支的变量聚合为一个变量，以实现下游节点统一配置。
 * 在工作流中，当多个分支产生不同的变量，而后续节点需要统一处理这些变量时，会使用此节点进行变量聚合。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class VariableAggregator extends AbstractWorkflowNode {
    // 将多路分支的变量聚合为一个变量，以实现下游节点统一配置。
}