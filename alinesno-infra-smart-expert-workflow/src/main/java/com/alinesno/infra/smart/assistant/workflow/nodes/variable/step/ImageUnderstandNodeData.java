package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片生成
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageUnderstandNodeData extends NodeData {

    // 添加 llmModelId 字段，用于存储大语言模型的 ID
    private String llmModelId;

    // 添加 prompt 字段，用于存储提示信息
    private String prompt;

}

