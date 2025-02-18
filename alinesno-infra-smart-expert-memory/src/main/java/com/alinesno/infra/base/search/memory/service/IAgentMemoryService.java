package com.alinesno.infra.base.search.memory.service;

import com.alinesno.infra.base.search.memory.bean.AgentMemoryDto;
import com.alinesno.infra.base.search.memory.bean.MemoryNodeWithScore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IAgentMemoryService {

    /**
     * 批量添加记忆数据
     * @param dtos
     */
    void addBatchMemoryData(List<AgentMemoryDto> dtos , AgentMemoryDto dto);

    /**
     * 通过AgentID查询出记忆数据,默认前20条记忆，通过时间倒叙
     *
     * @param agentId
     * @param targetId
     * @param memoryText
     * @return
     */
    List<MemoryNodeWithScore> queryMemoryData(String agentId, String targetId , String memoryText);
}