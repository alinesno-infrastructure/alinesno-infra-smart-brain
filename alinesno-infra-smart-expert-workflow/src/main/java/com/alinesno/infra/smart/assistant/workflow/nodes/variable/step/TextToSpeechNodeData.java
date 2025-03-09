package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文本转语音（TextToSpeech）的节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TextToSpeechNodeData extends NodeData {

    /**
     * 大语言模型的 ID，用于指定在文本转语音过程中使用的模型
     */
    private String llmModelId;

    /**
     * 内容列表，存储文本转语音操作所涉及的文本内容相关标识或信息
     * 例如可能是文本内容的唯一标识或相关结果标识等
     */
    private List<String> contentList;

}