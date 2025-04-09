package com.alinesno.infra.smart.assistant.workplace.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceResponseDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceItemEntity;

public interface IWorkplaceItemService extends IBaseService<WorkplaceItemEntity> {

    /**
     * 根据工作
     * @param id
     * @return
     */
    WorkplaceResponseDto getWorkplaceItem(Long id);

}
