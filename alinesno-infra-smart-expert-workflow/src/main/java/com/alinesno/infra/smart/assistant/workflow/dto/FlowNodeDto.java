package com.alinesno.infra.smart.assistant.workflow.dto;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * FlowNodeDto类用于表示工作流中的一个节点
 * 它包含了节点的基本信息，如ID、类型、坐标位置、属性以及与其相连的前后节点
 */
@NoArgsConstructor
@Data
@Slf4j
public class FlowNodeDto {

    private String id;
    private Long nodeId;
    private boolean isPrint;
    private boolean isLastNode;
    private String type;
    private String stepName;
    private int x;
    private int y;
    private Map<String, Object> properties;
    private List<FlowNodeDto> nextNodes;
    private List<FlowNodeDto> prevNodes;

    public boolean isLastNode() {
        return type.equals("end") ;
    }

    /**
     * 出边信息，记录每条边的 sourceAnchorId/targetAnchorId 以及目标节点
     */
    private List<FlowEdgeDto> outgoingEdges;

    public FlowNodeDto(String id, String type, int x, int y, Map<String, Object> properties) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.properties = properties;
        this.nextNodes = new ArrayList<>();
        this.prevNodes = new ArrayList<>();
        this.outgoingEdges = new ArrayList<>();
    }

    public String getStepName() {
        return String.valueOf(this.getProperties().getOrDefault("stepName", ""));
    }

    /**
     * 添加一个后继节点，并记录边的 anchor 信息
     */
    public void addNextNode(FlowNodeDto nextNode, String sourceAnchorId, String targetAnchorId) {
        if (nextNode == null) return;
        this.nextNodes.add(nextNode);
        nextNode.getPrevNodes().add(this);
        this.outgoingEdges.add(new FlowEdgeDto(sourceAnchorId, targetAnchorId, nextNode));
    }

    // 兼容老方法（如果外部还在使用）
    public void addNextNode(FlowNodeDto nextNode) {
        addNextNode(nextNode, null, null);
    }

    public FlowNodeEntity toEntity() {
        FlowNodeEntity entity = new FlowNodeEntity();
        entity.setId(IdUtil.getSnowflakeNextId());
        entity.setFlowId(null);
        entity.setStepId(this.getId());
        entity.setNodeName(this.getStepName());
        entity.setNodeType(this.getType());
        entity.setX(this.getX());
        entity.setY(this.getY());
        entity.setProperties(JSONObject.toJSONString(this.getProperties()));

        StringBuilder nextNodeIds = new StringBuilder();
        for (FlowNodeDto nextNode : this.getNextNodes()) {
            if (nextNodeIds.length() > 0) {
                nextNodeIds.append(",");
            }
            nextNodeIds.append(nextNode.getId());
        }
        entity.setNextNodeIds(nextNodeIds.toString());

        StringBuilder prevNodeIds = new StringBuilder();
        for (FlowNodeDto prevNode : this.getPrevNodes()) {
            if (prevNodeIds.length() > 0) {
                prevNodeIds.append(",");
            }
            prevNodeIds.append(prevNode.getId());
        }
        entity.setPrevNodeIds(prevNodeIds.toString());
        return entity;
    }

    public static FlowNodeDto fromEntity(FlowNodeEntity entity) {
        FlowNodeDto dto = new FlowNodeDto();
        dto.setId(entity.getStepId());
        dto.setNodeId(entity.getId());
        dto.setType(entity.getNodeType());
        dto.setStepName(entity.getNodeName());
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        try {
            if (entity.getProperties() != null) {
                Map<String, Object> propertiesMap = JSON.parseObject(entity.getProperties(), Map.class);
                dto.setProperties(propertiesMap);
            } else {
                dto.setProperties(new HashMap<>());
            }
        } catch (Exception e) {
            log.debug("Error converting properties to JSON: " + e.getMessage());
            dto.setProperties(new HashMap<>());
        }
        dto.setNextNodes(new ArrayList<>());
        dto.setPrevNodes(new ArrayList<>());
        dto.setOutgoingEdges(new ArrayList<>());
        return dto;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FlowNodeDto{").append("id='").append(id).append("', ").append("type='").append(type).append("', ").append("x=").append(x).append(", ").append("y=").append(y).append(", ").append("properties=").append(properties).append(", nextNodes=[");
        for (int i = 0; i < nextNodes.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(nextNodes.get(i).getId());
        }
        sb.append("], outgoingEdges=[");
        for (int i = 0; i < outgoingEdges.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(outgoingEdges.get(i).getSourceAnchorId()).append("->").append(outgoingEdges.get(i).getTargetNode().getId());
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowNodeDto that = (FlowNodeDto) o;
        if (x != that.x) return false;
        if (y != that.y) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(type, that.type)) return false;
        return Objects.equals(properties, that.properties);
    }

    @Data
    public static class FlowEdgeDto {
        private String sourceAnchorId;
        private String targetAnchorId;
        private FlowNodeDto targetNode;

        public FlowEdgeDto(String sourceAnchorId, String targetAnchorId, FlowNodeDto targetNode) {
            this.sourceAnchorId = sourceAnchorId;
            this.targetAnchorId = targetAnchorId;
            this.targetNode = targetNode;
        }
    }
}