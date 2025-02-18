package com.alinesno.infra.base.search.memory.service;

import com.alinesno.infra.base.search.memory.IMemoryBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Memory 接口定义了一个记忆系统的操作规范，包括添加、获取和列出记忆。
 */
@Slf4j
@Service
public class MemoryService implements IMemoryBase {

    @Override
    public Map<String, Object> get(String memoryId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getAll() {
        return null;
    }

    @Override
    public Map<String, Object> update(String memoryId, Map<String, Object> data) {
        return null;
    }

    @Override
    public void delete(String memoryId) {

    }

    @Override
    public List<Map<String, Object>> history(String memoryId) {
        return null;
    }

//    @Override
//    public void addMemoryData(AgentMemoryDto dto) {
//        agentMemoryService.addMemoryData(dto);
//    }

}