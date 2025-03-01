package com.alinesno.infra.smart.assistant.workflow.parse;

import com.alinesno.infra.smart.assistant.workflow.dto.FlowNodeDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LogicFlowParser 类用于解析工作流数据中的节点和边信息，
 * 并构建节点之间的连接关系，最终返回一个包含所有节点信息的列表。
 */
public class LogicFlowParser {

    /**
     * 解析工作流数据中的节点和边信息，构建节点之间的连接关系。
     *
     * @param data 包含节点和边信息的 Map 对象，其中 "nodes" 键对应节点列表，"edges" 键对应边列表。
     * @return 包含所有节点信息的列表，节点之间的连接关系已构建好。
     */
    public static List<FlowNodeDto> parseNodes(Map<String, Object> data) {
        // 存储节点 ID 到节点对象的映射，方便后续查找节点
        Map<String, FlowNodeDto> nodeMap = new HashMap<>();
        // 存储源节点 ID 到目标节点 ID 列表的映射，用于构建节点之间的连接关系
        Map<String, List<String>> edgeMap = new HashMap<>();

        // 解析节点信息
        // 从传入的数据中获取节点列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
        if (nodes != null) {
            // 遍历每个节点数据
            for (Map<String, Object> nodeData : nodes) {
                // 获取节点的 ID
                String id = (String) nodeData.get("id");
                // 获取节点的类型
                String type = (String) nodeData.get("type");
                // 获取节点的 x 坐标
                int x = (int) nodeData.get("x");
                // 获取节点的 y 坐标
                int y = (int) nodeData.get("y");
                // 获取节点的属性信息
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) nodeData.get("properties");
                // 创建节点对象
                FlowNodeDto node = new FlowNodeDto(id, type, x, y, properties);
                // 将节点对象存入映射中
                nodeMap.put(id, node);
            }
        }

        // 解析边信息
        // 从传入的数据中获取边列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> edges = (List<Map<String, Object>>) data.get("edges");
        if (edges != null) {
            // 遍历每条边数据
            for (Map<String, Object> edgeData : edges) {
                // 获取边的源节点 ID
                String sourceNodeId = (String) edgeData.get("sourceNodeId");
                // 获取边的目标节点 ID
                String targetNodeId = (String) edgeData.get("targetNodeId");
                // 将目标节点 ID 添加到源节点对应的目标节点列表中
                edgeMap.computeIfAbsent(sourceNodeId, k -> new ArrayList<>()).add(targetNodeId);
            }
        }

        // 构建节点之间的关系
        // 遍历边映射，为每个源节点添加对应的目标节点
        for (Map.Entry<String, List<String>> entry : edgeMap.entrySet()) {
            String sourceNodeId = entry.getKey();
            FlowNodeDto sourceNode = nodeMap.get(sourceNodeId);
            if (sourceNode != null) {
                // 遍历源节点对应的目标节点列表
                for (String targetNodeId : entry.getValue()) {
                    FlowNodeDto targetNode = nodeMap.get(targetNodeId);
                    if (targetNode != null) {
                        // 为源节点添加下一个节点
                        sourceNode.addNextNode(targetNode);
                    }
                }
            }
        }

        // 返回所有节点的列表
        return new ArrayList<>(nodeMap.values());
    }
}