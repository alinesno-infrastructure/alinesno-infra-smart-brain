// QuestionClassifier.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是问题分类节点类，继承自 AbstractWorkflowNode 类。
 * 该节点通过定义分类描述，让大语言模型（LLM）能够根据用户输入选择与之相匹配的分类。
 * 在工作流中，当需要对用户的问题进行分类以便后续进行针对性处理时，会使用此节点完成问题分类任务。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class QuestionClassifier extends AbstractWorkflowNode {
    // 通过定义分类描述，LLM 能够根据用户输入选择与之相匹配的分类。
}