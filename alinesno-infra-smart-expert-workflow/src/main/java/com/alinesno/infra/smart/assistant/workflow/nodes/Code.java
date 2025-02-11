// Code.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是代码执行节点类，继承自 AbstractWorkflowNode 类。
 * 该节点用于运行 Python / NodeJS 代码以在工作流程中执行数据转换等自定义逻辑。
 * 在工作流中，如果需要进行一些特定的数据处理、算法执行等操作，会使用此节点来运行相应的代码。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class Code extends AbstractWorkflowNode {
    // 运行 Python / NodeJS 代码以在工作流程中执行数据转换等自定义逻辑。
}