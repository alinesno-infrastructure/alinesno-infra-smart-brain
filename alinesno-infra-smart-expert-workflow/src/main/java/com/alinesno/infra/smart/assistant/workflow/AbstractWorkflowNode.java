// AbstractWorkflowNode.java
package com.alinesno.infra.smart.assistant.workflow;

import lombok.Data;

/**
 * 这是一个抽象父类，继承自 WorkflowNode 接口。
 * 它作为所有具体工作流节点类的基类，包含了所有工作流节点可能通用的属性和方法。
 * 由于它是抽象类，不能直接实例化，具体的工作流节点类需要继承该类并实现必要的逻辑。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public abstract class AbstractWorkflowNode implements WorkflowNode {
    // 父类可以包含所有节点通用的属性和方法
}