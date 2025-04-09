package com.alinesno.infra.smart.assistant.workplace.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceAddDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceEntity;

public interface IWorkplaceService extends IBaseService<WorkplaceEntity> {

    /**
     * 创建工作台
     * @param dto
     * @return
     */
    Long createWorkplace(WorkplaceAddDto dto);
}
