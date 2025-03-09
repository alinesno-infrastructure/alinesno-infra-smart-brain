package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文档相关的节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentExtractNodeData extends NodeData {

    // documentList 字段，用于存储文档列表
    private List<String> documentList;
}