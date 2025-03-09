package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 语音转文本的节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpeechToTextNodeData extends NodeData {

    /**
     * 大语言模型的 ID，用于指定在语音转文本过程中使用的模型
     */
    private String llmModelId;

    /**
     * 音频列表，存储语音转文本操作所涉及的音频相关标识或信息
     */
    private List<String> audioList;

}