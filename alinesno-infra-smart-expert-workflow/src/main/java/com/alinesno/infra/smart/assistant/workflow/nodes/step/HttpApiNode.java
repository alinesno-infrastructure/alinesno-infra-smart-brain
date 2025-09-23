// HttpApiNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.HttpApiNodeData;
import com.alinesno.infra.smart.assistant.workflow.utils.HttpApiRequestor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * HttpApiNode类，继承自AbstractFlowNode，表示一个HTTP API节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "http_api")
@EqualsAndHashCode(callSuper = true)
public class HttpApiNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "http_api"。
     */
    public HttpApiNode() {
        setType("http_api");
    }

    @Override
    protected CompletableFuture<Void> handleNode() {
        try {
            HttpApiNodeData nodeData = getNodeData();
            log.debug("nodeData = {}", nodeData);
            log.debug("node type = {} output = {}", node.getType(), output);

            // 发送请求并获取结果
            String result = HttpApiRequestor.sendRequest(nodeData);
            log.debug("http api result = {}", result);

            // 触发节点事件
            eventNodeMessage(result);

            // 将实际响应结果放入输出
            output.put(node.getStepName() + ".response", result);

            // 根据节点配置决定是否回显
            if (node.isPrint()) {
                eventMessageCallbackMessage(result);
            }

            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            log.error("HttpApiNode 执行异常: {}", ex.getMessage(), ex);
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }

    private HttpApiNodeData getNodeData() {
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data"));
        return JSONObject.parseObject(nodeDataJson, HttpApiNodeData.class);
    }
}