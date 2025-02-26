package com.alinesno.infra.smart.assistant.workflow.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 解析 JSON 数据并构建节点列表的类
public class LogicFlowParser {

    public static List<FlowNodeDto> parseNodes(Map<String, Object> data) {
        // 存储所有节点的映射，方便根据 ID 查找节点
        Map<String, FlowNodeDto> nodeMap = new HashMap<>();
        // 存储所有边的映射，键为源节点 ID，值为目标节点 ID 列表
        Map<String, List<String>> edgeMap = new HashMap<>();

        // 解析节点
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
        for (Map<String, Object> nodeData : nodes) {
            String id = (String) nodeData.get("id");
            String type = (String) nodeData.get("type");
            int x = (int) nodeData.get("x");
            int y = (int) nodeData.get("y");
            Map<String, Object> properties = (Map<String, Object>) nodeData.get("properties");
            FlowNodeDto node = new FlowNodeDto(id, type, x, y, properties);
            nodeMap.put(id, node);
        }

        // 解析边
        List<Map<String, Object>> edges = (List<Map<String, Object>>) data.get("edges");
        for (Map<String, Object> edgeData : edges) {
            String sourceNodeId = (String) edgeData.get("sourceNodeId");
            String targetNodeId = (String) edgeData.get("targetNodeId");
            edgeMap.computeIfAbsent(sourceNodeId, k -> new ArrayList<>()).add(targetNodeId);
        }

        // 构建节点之间的关系
        for (Map.Entry<String, List<String>> entry : edgeMap.entrySet()) {
            String sourceNodeId = entry.getKey();
            FlowNodeDto sourceNode = nodeMap.get(sourceNodeId);
            for (String targetNodeId : entry.getValue()) {
                FlowNodeDto targetNode = nodeMap.get(targetNodeId);
                sourceNode.addNextNode(targetNode);
            }
        }

        // 返回所有节点的列表
        return new ArrayList<>(nodeMap.values());
    }

//    public static void main(String[] args) {
//        // 这里应该是你从 JSON 解析得到的 Map 数据
//        Map<String, Object> data = new HashMap<>();
//        // 这里需要填充实际的节点和边数据
//        // 示例数据省略，你可以使用 JSON 解析库将 JSON 字符串转换为 Map
//
//        List<FlowNodeDto> nodes = parseNodes(data);
//        for (FlowNodeDto node : nodes) {
//            System.out.println("Node ID: " + node.id + ", Type: " + node.type);
//            for (FlowNodeDto nextNode : node.nextNodes) {
//                System.out.println("  Next Node ID: " + nextNode.id);
//            }
//        }
//    }

}