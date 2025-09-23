package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.EndNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 结束节点
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "end")
@EqualsAndHashCode(callSuper = true)
public class EndNode extends AbstractFlowNode {

    public EndNode() {
        setType("end");
    }

    /**
     * 轻量同步处理并返回已完成的 CompletableFuture。
     * 该方法会在上层提交的线程（如 chatThreadPool）中执行，不会额外创建线程。
     */
    @Override
    protected CompletableFuture<Void> handleNode() {
        try {
            EndNodeData nodeData = getNodeData();

            String answer = nodeData.getReplayContent();

            answer = replacePlaceholders(answer);
            eventNodeMessage(answer);

            // 设置参数到 output（与原逻辑一致）
            output.put(node.getStepName() + ".output", answer);
            taskInfo.setFullContent(answer);

            // 轻量操作：直接返回已完成的 Future
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            log.error("StartNode 执行异常: {}", ex.getMessage(), ex);
            // 将错误信息写入 output 以便后续处理
            output.put(node.getStepName() + ".output", "处理异常: " + ex.getMessage());
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }

    private EndNodeData getNodeData() {
        String nodeDataJson = String.valueOf(node.getProperties().get("node_data"));
        return JSONObject.parseObject(nodeDataJson, EndNodeData.class);
    }
}