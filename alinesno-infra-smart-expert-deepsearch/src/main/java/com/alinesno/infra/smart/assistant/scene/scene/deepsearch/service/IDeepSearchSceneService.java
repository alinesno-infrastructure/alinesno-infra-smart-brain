package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DeepSearchSceneEntity;

import java.util.List;

public interface IDeepSearchSceneService extends IBaseService<DeepSearchSceneEntity> {

    /**
     * 更新场景的AI角色
     * @param dto
     */
    void updateSceneAgents(UpdateSceneAgentDto dto);

    /**
     * 获取场景的AI角色列表
     * @param dto
     * @return
     */
    List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto);

    /**
     * 根据场景ID获取场景的AI角色
     * @param id
     * @param query
     * @return
     */
    DeepSearchSceneEntity getBySceneId(long id, PermissionQuery query);
}
