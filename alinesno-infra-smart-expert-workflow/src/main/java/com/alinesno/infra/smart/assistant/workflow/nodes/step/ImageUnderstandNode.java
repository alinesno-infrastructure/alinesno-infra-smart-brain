// ImageUnderstandNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示图片理解节点，继承自 AbstractFlowNode 类。
 * 主要功能是识别出图片中的对象、场景等信息，并根据这些信息回答用户问题。
 * 在工作流中，当需要对图片进行内容理解时会使用该节点。
 */
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "image_understand")
@EqualsAndHashCode(callSuper = true)
public class ImageUnderstandNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "image_understand"。
     */
    public ImageUnderstandNode() {
        setType("image_understand");
    }

    @Override
    protected void handleNode() {

    }
}