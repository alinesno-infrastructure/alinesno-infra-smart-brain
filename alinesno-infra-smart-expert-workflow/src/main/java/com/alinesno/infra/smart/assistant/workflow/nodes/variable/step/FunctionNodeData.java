package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 函数节点数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FunctionNodeData extends NodeData {

    // 原始脚本内容
    private String rawScript;
    // 参数列表
    private List<NodeParams> params;

    @Data
    public static class NodeParams {
        // 输入数据列表
        private List<String> inputData;
        // 参数名称
        private String name;
        // 参数类型
        private String type;
    }
}
