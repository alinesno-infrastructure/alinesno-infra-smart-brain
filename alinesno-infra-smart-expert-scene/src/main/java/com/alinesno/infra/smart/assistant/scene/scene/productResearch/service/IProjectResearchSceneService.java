package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ProjectResearchSceneEntity;

import java.util.List;

public interface IProjectResearchSceneService extends IBaseService<ProjectResearchSceneEntity> {

    /**
     * 更新场景代理
     * @param dto
     */
    void updateSceneAgents(UpdateSceneAgentDto dto);

    /**
     * 获取角色列表
     * @param dto
     * @return
     */
    List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto);

    /**
     * 根据场景ID获取场景信息
     * @param sceneId
     * @param query
     * @return
     */
    ProjectResearchSceneEntity getBySceneId(long sceneId, PermissionQuery query);

}    