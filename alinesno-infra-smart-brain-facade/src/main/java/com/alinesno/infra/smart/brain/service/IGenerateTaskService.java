package com.alinesno.infra.smart.brain.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;

import java.util.List;

public interface IGenerateTaskService extends IBaseService<GenerateTaskEntity> {

    void commitTask(BrainTaskDto dto);

    void commitTaskToCms(BrainTaskDto dto);

    List<GenerateTaskEntity> getAllUnfinishedTasks();
}
