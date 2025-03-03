// ReplyNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * HttpApiNode类，继承自AbstractFlowNode，表示一个HTTP API节点。
 */
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "http_api")
@EqualsAndHashCode(callSuper = true)
public class HttpApiNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "reply"。
     */
    public HttpApiNode() {
        setType("http_api");
    }

    @Override
    protected void handleNode() {

    }
}