package com.alinesno.infra.smart.assistant.workflow.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 节点类
public class FlowNodeDto {
    String id;
    String type;
    int x;
    int y;
    Map<String, Object> properties;
    List<FlowNodeDto> nextNodes;

    public FlowNodeDto(String id, String type, int x, int y, Map<String, Object> properties) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.properties = properties;
        this.nextNodes = new ArrayList<>();
    }

    public void addNextNode(FlowNodeDto nextNode) {
        this.nextNodes.add(nextNode);
    }
}

