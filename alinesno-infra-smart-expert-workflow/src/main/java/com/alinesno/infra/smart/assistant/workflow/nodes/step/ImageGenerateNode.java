package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.ImageGenerateNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 该类表示图片生成节点，继承自 AbstractFlowNode 类。
 * 其功能是根据提供的文本内容生成相应的图片。
 * 在工作流中，如果需要根据文本描述生成图片，就会用到这个节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "image_generate")
@EqualsAndHashCode(callSuper = true)
public class ImageGenerateNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "image_generate"。
     */
    public ImageGenerateNode() {
        setType("image_generate");
    }

    @Override
    protected CompletableFuture<Void> handleNode() {
        try {
            ImageGenerateNodeData nodeData = getNodeData();
            log.debug("nodeData = {}", nodeData);
            log.debug("node type = {} output = {}", node.getType(), output);

            // TODO: 调用真实的图片生成服务，基于 nodeData 构造请求
            String answer = "图片生成内容";
            String image = "图片生成地址";

            // 将结果写入输出上下文
            output.put(node.getStepName() + ".answer", answer);
            output.put(node.getStepName() + ".image", image);

            // 触发节点事件（例如用于日志或下游处理）
            eventNodeMessage(answer);

            // 根据节点配置决定是否回显给用户
            if (node.isPrint()) {
                eventMessageCallbackMessage(answer);
            }

            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            log.error("ImageGenerateNode 执行异常: {}", ex.getMessage(), ex);
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }

    private ImageGenerateNodeData getNodeData() {
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data"));
        return JSONObject.parseObject(nodeDataJson, ImageGenerateNodeData.class);
    }
}