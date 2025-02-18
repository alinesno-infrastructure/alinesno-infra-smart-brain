package com.alinesno.infra.base.search.memory;

import com.alinesno.infra.base.search.memory.bean.MemoryNode;
import com.alinesno.infra.base.search.memory.bean.MemoryNodeWithScore;

import java.util.List;

/**
 * 定义了处理记忆节点（MemoryNode）的记忆存储接口。
 * 它概述了基本操作，如检索、更新、刷新和关闭记忆范围。
 */
public interface BaseMemoryStore {

    /**
     * 创建记忆向量索引。
     */
    void createMemoryVectorIndex();

    /**
     * 批量插入记忆节点。
     *
     * @param nodes 要插入的记忆节点列表
     */
    void batchInsert(List<MemoryNode> nodes);

    /**
     * 根据代理ID和记忆文本搜索记忆节点。
     * @param agentIdLong
     * @param memoryText
     * @param i
     * @return
     */
    List<MemoryNodeWithScore> searchByAgentAndVector(Long agentIdLong, Long targetId , String memoryText, int i);

}