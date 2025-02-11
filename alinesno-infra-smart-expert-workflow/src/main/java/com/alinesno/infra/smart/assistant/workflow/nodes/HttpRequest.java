// HttpRequest.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import com.alinesno.infra.smart.assistant.workflow.AbstractWorkflowNode;
import lombok.Data;

/**
 * 这是 HTTP 请求节点类，继承自 AbstractWorkflowNode 类。
 * 该节点允许通过 HTTP 协议发送服务器请求，适用于获取外部检索结果、webhook、生成图片等情景。
 * 在工作流中，如果需要与外部服务器进行数据交互，会使用此节点发送 HTTP 请求并处理响应。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public class HttpRequest extends AbstractWorkflowNode {
    // 允许通过 HTTP 协议发送服务器请求，适用于获取外部检索结果、webhook、生成图片等情景。
}