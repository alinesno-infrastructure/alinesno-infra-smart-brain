// ImageGenerateNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 该类表示图片生成节点，继承自 AbstractFlowNode 类。
 * 其功能是根据提供的文本内容生成相应的图片。
 * 在工作流中，如果需要根据文本描述生成图片，就会用到这个节点。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImageGenerateNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "image_generate"。
     */
    public ImageGenerateNode() {
        setType("image_generate");
    }
}