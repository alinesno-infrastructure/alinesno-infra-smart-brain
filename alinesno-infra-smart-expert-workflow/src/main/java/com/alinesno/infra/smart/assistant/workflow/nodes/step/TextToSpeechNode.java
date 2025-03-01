// TextToSpeechNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 该类表示文本转语音节点，继承自 AbstractFlowNode 类。
 * 主要用于将文本通过语音合成模型转换为音频。
 * 在工作流中，当需要将文本信息以语音形式输出时，会使用该节点。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TextToSpeechNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "text_to_speech"。
     */
    public TextToSpeechNode() {
        setType("text_to_speech");
    }
}