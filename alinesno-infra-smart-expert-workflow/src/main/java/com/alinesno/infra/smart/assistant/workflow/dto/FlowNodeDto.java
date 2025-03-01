package com.alinesno.infra.smart.assistant.workflow.dto;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FlowNodeDto类用于表示工作流中的一个节点
 * 它包含了节点的基本信息，如ID、类型、坐标位置、属性以及与其相连的前后节点
 */
@NoArgsConstructor
@Data
@Slf4j
public class FlowNodeDto {

    // 节点唯一标识符
    private String id;
    // 节点类型，表示节点的种类，如开始节点、任务节点等
    private String type;

    // 节点名称
    private String stepName;
    // 节点在工作流图中的横坐标位置
    private int x;
    // 节点在工作流图中的纵坐标位置
    private int y;
    // 节点的属性，采用键值对的形式存储额外信息
    private Map<String, Object> properties;
    // 后续节点列表，表示当前节点之后的节点
    private List<FlowNodeDto> nextNodes;
    // 前置节点列表，表示当前节点之前的节点
    private List<FlowNodeDto> prevNodes;

    /**
     * 构造函数用于创建一个新的FlowNodeDto实例
     *
     * @param id         节点的唯一标识符
     * @param type       节点类型
     * @param x          节点的横坐标位置
     * @param y          节点的纵坐标位置
     * @param properties 节点的属性
     */
    public FlowNodeDto(String id, String type, int x, int y, Map<String, Object> properties) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.properties = properties;
        this.nextNodes = new ArrayList<>();
        this.prevNodes = new ArrayList<>();
    }

    /**
     * 获取节点名称的方法
     *
     * @return
     */
    public String getStepName() {
        return String.valueOf(this.getProperties().getOrDefault("stepName", ""));
    }

    /**
     * 添加后续节点的方法
     * 它不仅将给定的节点添加到当前节点的后续节点列表中，同时也会将当前节点添加到给定节点的前置节点列表中
     *
     * @param nextNode 要添加的后续节点
     */
    public void addNextNode(FlowNodeDto nextNode) {
        this.nextNodes.add(nextNode);
        nextNode.getPrevNodes().add(this);
    }

    /**
     * 将FlowNodeDto转换为FlowNodeEntity
     *
     * @return FlowNodeEntity实例
     */
    public FlowNodeEntity toEntity() {
        FlowNodeEntity entity = new FlowNodeEntity();
        // 假设这里可以获取到正确的 ID
        entity.setId(IdUtil.getSnowflakeNextId());
        entity.setFlowId(null); // 需根据业务逻辑设置

        entity.setNodeId(this.getId());
        entity.setNodeName(this.getStepName());
        entity.setNodeType(this.getType());
        entity.setX(this.getX());
        entity.setY(this.getY());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            entity.setProperties(objectMapper.writeValueAsString(this.getProperties()));
        } catch (JsonProcessingException e) {
            log.error("Error converting properties to JSON: " + e.getMessage());
            entity.setProperties(null);
        }

        StringBuilder nextNodeIds = new StringBuilder();
        for (FlowNodeDto nextNode : this.getNextNodes()) {
            if (!nextNodeIds.isEmpty()) {
                nextNodeIds.append(",");
            }
            nextNodeIds.append(nextNode.getId());
        }
        entity.setNextNodeIds(nextNodeIds.toString());

        StringBuilder prevNodeIds = new StringBuilder();
        for (FlowNodeDto prevNode : this.getPrevNodes()) {
            if (!prevNodeIds.isEmpty()) {
                prevNodeIds.append(",");
            }
            prevNodeIds.append(prevNode.getId());
        }
        entity.setPrevNodeIds(prevNodeIds.toString());

        return entity;
    }

    /**
     * 将FlowNodeEntity转换为FlowNodeDto
     *
     * @param entity FlowNodeEntity实例
     * @return FlowNodeDto实例
     */
    public static FlowNodeDto fromEntity(FlowNodeEntity entity) {
        FlowNodeDto dto = new FlowNodeDto();
        dto.setId(entity.getNodeId()) ;
        dto.setType(entity.getNodeType());
        dto.setStepName(entity.getNodeName());
        dto.setX(entity.getX());
        dto.setY(entity.getY());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (entity.getProperties() != null) {
                TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};
                dto.setProperties(objectMapper.readValue(entity.getProperties(), typeRef));
            } else {
                dto.setProperties(new HashMap<>());
            }
        } catch (Exception e) {
            log.debug("Error converting properties to JSON: " + e.getMessage());
            dto.setProperties(new HashMap<>());
        }

        return dto;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FlowNodeDto{")
                .append("id='").append(id).append("', ")
                .append("type='").append(type).append("', ")
                .append("x=").append(x).append(", ")
                .append("y=").append(y).append(", ")
                .append("properties=").append(properties)
                .append(", nextNodes=[");

        for (int i = 0; i < nextNodes.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            // 只输出下一个节点的 ID，避免递归调用
            sb.append(nextNodes.get(i).getId());
        }
        sb.append("]}");
        return sb.toString();
    }
}