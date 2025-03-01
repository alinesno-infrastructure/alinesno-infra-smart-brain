// FunctionNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 该类表示脚本功能节点，继承自 AbstractFlowNode 类。
 * 允许使用 Groovy 脚本进行编辑开发，以实现一些自定义的功能和逻辑。
 * 在工作流中，当需要执行特定的脚本逻辑时，会使用该节点。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FunctionNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "function"。
     */
    public FunctionNode() {
        setType("function");
    }
}