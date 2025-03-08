package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  问题节点数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionNodeData extends NodeData {

    // 添加 llmModelId 字段，用于存储大语言模型的 ID
    private String llmModelId;

    // 添加 isPrintResoning 字段，用于标识是否打印推理过程
    private boolean isPrintResoning;

    // 添加 historyNumber 字段，用于存储历史记录的数量
    private int historyNumber ;

    // 添加 prompt 字段，用于存储提示信息
    private String prompt;
}
