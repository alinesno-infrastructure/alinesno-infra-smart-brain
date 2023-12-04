package com.alinesno.infra.smart.brain.service;// FileProcessingService.java

import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;

/**
 * 文件处理服务接口
 */
public interface IFileProcessingService {
    /**
     * 处理文件的方法
     * @param brainTaskDto 文件路径
     * @return CompletableFuture<String> 异步返回处理结果
     */
    void processFile(GenerateTaskEntity brainTaskDto);
}