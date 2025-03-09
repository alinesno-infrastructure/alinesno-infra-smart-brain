package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 重排序器相关的节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RerankerNodeData extends NodeData {

    // rerankerReference 字段，用于存储重排序器的引用列表
    private List<String> rerankerReference;

    // rerankerSetting 字段，用于存储重排序器的设置
    private RerankerSetting rerankerSetting;

    // questionReference 字段，用于存储问题的引用列表
    private List<String> questionReference;

    // rerankerModelId 字段，用于存储重排序器模型的 ID
    private String rerankerModelId;

    /**
     * 重排序器设置的内部类
     */
    @Data
    public static class RerankerSetting {
        // topK 字段，用于存储前 K 个结果
        private int topK;

        // minRelevance 字段，用于存储最小相关性
        private String minRelevance;

        // quoteLimit 字段，用于存储引用限制
        private int quoteLimit;
    }
}