// SpeechToTextNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示语音转文本节点，继承自 AbstractFlowNode 类。
 * 其功能是将音频通过语音识别模型转换为文本。
 * 在工作流中，当需要处理语音输入并转换为可处理的文本信息时，会使用该节点。
 */
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "speech_to_text")
@EqualsAndHashCode(callSuper = true)
public class SpeechToTextNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "speech_to_text"。
     */
    public SpeechToTextNode() {
        setType("speech_to_text");
    }
}