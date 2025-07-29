package com.alinesno.infra.smart.assistant.workplace.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceAddDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceEntity;
import com.alinesno.infra.smart.im.dto.CollectItemDto;

import java.util.List;

public interface IWorkplaceService extends IBaseService<WorkplaceEntity> {

    /**
     * 创建工作台
     * @param dto
     * @return
     */
    Long createWorkplace(WorkplaceAddDto dto);

    /**
     * 查询组织工作台，包括组织当前的工作台和公共的工作台
     * @param orgId
     * @return
     */
    List<WorkplaceEntity> listOrgWorkplace(Long orgId);

    /**
     * 删除工作台
     * @param ids
     * @return
     */
    void deleteWorkplace(String ids);

}
