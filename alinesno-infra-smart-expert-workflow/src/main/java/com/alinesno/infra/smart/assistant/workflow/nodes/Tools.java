// Tools.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是工具节点类，继承自 AbstractWorkflowNode 类。
 * 该节点允许在工作流内调用 Dify 内置工具、自定义工具、子工作流等。
 * 在工作流中，如果需要使用一些特定的工具或子流程来完成特定任务，会使用此节点调用相应的工具或子工作流。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class Tools extends AbstractWorkflowNode {
    // 允许在工作流内调用 Dify 内置工具、自定义工具、子工作流等。
}