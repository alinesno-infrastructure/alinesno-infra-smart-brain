package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AiChatNodeData 类，用于存储 AI 聊天节点的数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AiChatNodeData extends NodeData {
    // 添加 llmModelId 字段，用于存储大语言模型的 ID
    private String llmModelId;

    // 添加 isPrintResoning 字段，用于标识是否打印推理过程
    private boolean isPrintResoning;

    // 添加 prompt 字段，用于存储提示信息
    private String prompt;
}