package com.alinesno.infra.smart.brain.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;

import java.util.List;

public interface IGenerateTaskService extends IBaseService<GenerateTaskEntity> {

    /**
     * 提交任务
     *
     * @param dto
     * @return
     */
    Long commitTask(BrainTaskDto dto);

    /**
     * 查询所有未完成的任务
     * @return
     */
    List<GenerateTaskEntity> getAllUnfinishedTasks(int retryCount);

    /**
     * 查询出所有进行超时而且异常的任务
     * @param jobOutTime
     * @return
     */
    List<GenerateTaskEntity> getAllUnstableTasks(int jobOutTime);

}
