package com.alinesno.infra.smart.assistant.workflow.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 工作流请求数据传输对象
 * 用于封装工作流的节点和边信息
 */
@NoArgsConstructor
@Data
public class WorkflowRequestDto {
    /**
     * 工作流节点列表
     * 每个节点表示工作流中的一个步骤或任务，使用Map存储节点的属性
     */
    private List<Map<String, Object>> nodes;

    /**
     * 工作流边列表
     * 每条边表示节点之间的连接或顺序，使用Map存储边的属性
     */
    private List<Map<String, Object>> edges;
}
