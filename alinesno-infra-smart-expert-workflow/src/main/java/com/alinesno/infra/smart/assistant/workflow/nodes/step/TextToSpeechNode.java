package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.TextToSpeechNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 文本转语音节点（改为异步链签名）
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "text_to_speech")
@EqualsAndHashCode(callSuper = true)
public class TextToSpeechNode extends AbstractFlowNode {

    public TextToSpeechNode() {
        setType("text_to_speech");
    }

    /**
     * 说明：
     * - 该实现为轻量同步执行并返回已完成的 CompletableFuture，
     *   因为 executeNode(...) 是由上层在 chatThreadPool 中调用的（FlowServiceImpl.executeFlowNode 已用 chatThreadPool 提交）。
     * - 若实际 TTS 需要远端调用或耗时合成，请把耗时部分改为异步（CompletableFuture.supplyAsync(..., ttsExecutor)）。
     */
    @Override
    protected CompletableFuture<Void> handleNode() {
        try {
            TextToSpeechNodeData nodeData = getNodeData();
            log.debug("TextToSpeechNode nodeData = {}", nodeData);
            log.debug("node type = {} output = {}", node.getType(), output);

            // 这里示例为轻量处理：生成语音地址（或从配置/外部服务获取）
            String result = "语音地址"; // TODO: replace with real TTS invocation or URL

            // 将结果写入 output（遵循原有约定）
            output.put(node.getStepName() + ".result", result);

            // 若需要把内容追加到 outputContent（打印输出），可以判断 node.isPrint()
            if (node.isPrint()) {
                try {
                    eventMessageCallbackMessage(result);
                } catch (Exception e) {
                    log.warn("追加输出失败: {}", e.getMessage(), e);
                }
            }

            // 轻量操作，直接返回已完成的 Future（在父线程池线程执行）
            return CompletableFuture.completedFuture(null);

        } catch (Exception ex) {
            log.error("TextToSpeechNode 执行异常: {}", ex.getMessage(), ex);
            // 将错误信息放到 output 中，便于后续流程判断
            output.put(node.getStepName() + ".result", "TTS error: " + ex.getMessage());
            // 将异常转换为已完成的 future（也可返回 completeExceptionally）
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }

    private TextToSpeechNodeData getNodeData() {
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data"));
        return JSONObject.parseObject(nodeDataJson, TextToSpeechNodeData.class);
    }
}