package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 回复相关的节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EndNodeData extends NodeData {

    // replayParams 字段，用于存储回复的参数列表
    private List<String> replayParams;

    // replayContext 字段，用于存储回复的内容
    private String replayContent;

    // replayType 字段，用于存储回复的类型
    private String replayType;
}