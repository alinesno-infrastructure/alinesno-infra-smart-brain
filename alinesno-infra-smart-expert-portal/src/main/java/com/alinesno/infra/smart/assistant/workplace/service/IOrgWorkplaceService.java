package com.alinesno.infra.smart.assistant.workplace.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workplace.dto.OrgWorkplaceDto;
import com.alinesno.infra.smart.assistant.workplace.entity.OrgWorkplaceEntity;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceEntity;
import com.alinesno.infra.smart.im.dto.CustomizeWorkbenchDTO;

public interface IOrgWorkplaceService extends IBaseService<OrgWorkplaceEntity> {

    /**
     * 组织使用指定的工作台，处理过程
     * 1. 通过workplaceId复制工作台和工作台关联的Item到当前组织下面，然后修改名称，
     * 2. 如果是channel频道和scene场景的话，则复制该频道和场景到当前组织下面，然后指向Agent对象信息。
     * 3. 在OrgWorkplace表中添加一条记录，表示当前组织已经有使用的工作台
     */
    void useWorkplace(OrgWorkplaceDto dto);

    /**
     * 判断当前组织是否已经配置工作台
     * @param orgId
     * @return
     */
    Long isHasWorkplace(Long orgId);

    /**
     * 获取当前组织使用的工作台
     * @param orgId
     * @return
     */
    WorkplaceEntity getCurrentWorkplace(Long orgId);

    /**
     * 组织自定义工作台
     * @param dto
     */
    void customizeWorkbench(CustomizeWorkbenchDTO dto);

    /**
     * 判断当前用户是否已经配置工作台
     * @param userId
     * @param orgId
     * @return
     */
    Long isHasAccountWorkplace(Long userId , Long orgId);
}
