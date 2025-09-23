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

    @SuppressWarnings("unchecked")
    public static List<FlowNodeDto> parseNodes(Map<String, Object> data) {
        Map<String, FlowNodeDto> nodeMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
        if (nodes != null) {
            for (Map<String, Object> nodeData : nodes) {
                String id = (String) nodeData.get("id");
                String type = (String) nodeData.get("type");
                // x,y 在 json 中通常是 Number -> to int safely
                Object xObj = nodeData.get("x");
                Object yObj = nodeData.get("y");
                int x = xObj instanceof Number ? ((Number) xObj).intValue() : 0;
                int y = yObj instanceof Number ? ((Number) yObj).intValue() : 0;
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) nodeData.get("properties");
                FlowNodeDto node = new FlowNodeDto(id, type, x, y, properties == null ? new HashMap<>() : properties);
                nodeMap.put(id, node);
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> edges = (List<Map<String, Object>>) data.get("edges");
        if (edges != null) {
            for (Map<String, Object> edgeData : edges) {
                String sourceNodeId = (String) edgeData.get("sourceNodeId");
                String targetNodeId = (String) edgeData.get("targetNodeId");
                String sourceAnchorId = edgeData.get("sourceAnchorId") != null ? String.valueOf(edgeData.get("sourceAnchorId")) : null;
                String targetAnchorId = edgeData.get("targetAnchorId") != null ? String.valueOf(edgeData.get("targetAnchorId")) : null;

                FlowNodeDto sourceNode = nodeMap.get(sourceNodeId);
                FlowNodeDto targetNode = nodeMap.get(targetNodeId);
                if (sourceNode != null && targetNode != null) {
                    // 保存 next + prev，并记录 anchor 信息
                    sourceNode.addNextNode(targetNode, sourceAnchorId, targetAnchorId);
                }
            }
        }

        return new ArrayList<>(nodeMap.values());
    }
}