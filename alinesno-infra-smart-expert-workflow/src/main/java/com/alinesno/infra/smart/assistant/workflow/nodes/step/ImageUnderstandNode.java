package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.ImageUnderstandNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 该类表示图片理解节点，继承自 AbstractFlowNode 类。
 * 主要功能是识别出图片中的对象、场景等信息，并根据这些信息回答用户问题。
 * 在工作流中，当需要对图片进行内容理解时会使用该节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "image_understand")
@EqualsAndHashCode(callSuper = true)
public class ImageUnderstandNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "image_understand"。
     */
    public ImageUnderstandNode() {
        setType("image_understand");
    }

    @Override
    protected CompletableFuture<Void> handleNode() {
        try {
            ImageUnderstandNodeData nodeData = getNodeData();
            log.debug("nodeData = {}", nodeData);
            log.debug("node type = {} output = {}", node.getType(), output);

            // TODO: 调用实际的图片理解服务并基于 nodeData 解析图片内容
            String answer = "图片理解内容";

            output.put(node.getStepName() + ".answer", answer);

            // 触发节点事件（例如用于日志或下游处理）
            eventNodeMessage(answer);

            // 根据节点配置决定是否回显给用户
            if (node.isPrint()) {
                eventMessageCallbackMessage(answer);
            }

            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            log.error("ImageUnderstandNode 执行异常: {}", ex.getMessage(), ex);
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }

    private ImageUnderstandNodeData getNodeData() {
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data"));
        return JSONObject.parseObject(nodeDataJson, ImageUnderstandNodeData.class);
    }
}