package com.alinesno.infra.smart.brain.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.entity.AiGenTaskEntity;

public interface IAiGenTaskService extends IBaseService<AiGenTaskEntity> {

    void commitTask(BrainTaskDto dto);

    void commitTaskToCms(BrainTaskDto dto);

}
