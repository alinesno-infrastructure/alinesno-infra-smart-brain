package com.alinesno.infra.smart.assistant.workplace.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceImResponseDto;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceItemDto;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceResponseDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceItemEntity;

public interface IWorkplaceItemService extends IBaseService<WorkplaceItemEntity> {

    /**
     * 根据工作
     * @param id
     * @return
     */
    WorkplaceResponseDto getWorkplaceItem(Long id);

    /**
     * 更新工作台元素信息
      * @param dto
     * @return
     */
    boolean updateWorkplaceItem(WorkplaceItemDto dto);

    /**
     * 根据工作台ID和类型获取工作台元素信息
     * @param workplaceId
     * @param type
     * @return
     */
    WorkplaceImResponseDto getWorkplaceItemByType(Long workplaceId, String type);
}
