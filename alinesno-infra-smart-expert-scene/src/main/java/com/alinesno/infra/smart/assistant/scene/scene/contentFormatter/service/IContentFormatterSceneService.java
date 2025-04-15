package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterSceneEntity;

import java.util.List;

public interface IContentFormatterSceneService extends IBaseService<ContentFormatterSceneEntity> {

    /**
     * 根据场景ID获取场景信息
     * @param id
     * @param query
     * @return
     */
    ContentFormatterSceneEntity getBySceneId(long id, PermissionQuery query);

    /**
     * 更新场景的智能助手
     * @param dto
     */
    void updateSceneAgents(UpdateSceneAgentDto dto);

    /**
     * 获取智能助手角色列表
     *
     * @param dto
     * @return
     */
    List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto);
}