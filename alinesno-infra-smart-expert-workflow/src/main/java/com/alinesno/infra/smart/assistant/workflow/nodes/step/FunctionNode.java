// FunctionNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 该类表示脚本功能节点，继承自 AbstractFlowNode 类。
 * 允许使用 Groovy 脚本进行编辑开发，以实现一些自定义的功能和逻辑。
 * 在工作流中，当需要执行特定的脚本逻辑时，会使用该节点。
 */
@Slf4j
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "function")
@EqualsAndHashCode(callSuper = true)
public class FunctionNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "function"。
     */
    public FunctionNode() {
        setType("function");
    }

    @SneakyThrows
    @Override
    protected void handleNode() {
        log.debug( "node type = {} output = {}" , node.getType() , output) ;

        String prompt = "你是谁." ;

        CompletableFuture<String> future = getStringCompletableFuture(prompt);

        String output = future.get();
        log.debug("output = {}" , output);

    }

}