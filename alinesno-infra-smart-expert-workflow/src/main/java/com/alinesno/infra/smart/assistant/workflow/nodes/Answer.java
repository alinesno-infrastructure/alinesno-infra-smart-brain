// Answer.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是聊天流程中的回复节点类，继承自 AbstractWorkflowNode 类。
 * 该节点用于定义一个 Chatflow 流程中的回复内容。
 * 在聊天流程中，当需要对用户的输入做出响应时，此节点会提供相应的回复文本或信息。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class Answer extends AbstractWorkflowNode {
    // 定义一个 Chatflow 流程中的回复内容。
}