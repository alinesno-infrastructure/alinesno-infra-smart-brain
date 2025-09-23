package com.alinesno.infra.smart.assistant.workplace.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceItemDto;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceResponseDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceItemEntity;
import com.alinesno.infra.smart.im.dto.CollectItemDto;
import com.alinesno.infra.smart.im.dto.CollectItemObjectDto;

import java.util.List;

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
    List<CollectItemObjectDto> getWorkplaceItemByType(Long workplaceId, String type);

    /**
     * 添加收藏项目
     * @param dto
     * @param workplaceId
     */
    void addCollectItem(CollectItemDto dto, Long workplaceId);

    /**
     * 删除收藏项目
     * @param dto
     * @param workplaceId
     */
    void removeCollectItem(CollectItemDto dto, Long workplaceId);
}
