// Template.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是模板转换节点类，继承自 AbstractWorkflowNode 类。
 * 该节点允许借助 Jinja2 的 Python 模板语言灵活地进行数据转换、文本处理等。
 * 在工作流中，如果需要对数据进行格式化、文本生成等操作，可以使用此节点结合 Jinja2 模板来完成。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class Template extends AbstractWorkflowNode {
    // 允许借助 Jinja2 的 Python 模板语言灵活地进行数据转换、文本处理等。
}