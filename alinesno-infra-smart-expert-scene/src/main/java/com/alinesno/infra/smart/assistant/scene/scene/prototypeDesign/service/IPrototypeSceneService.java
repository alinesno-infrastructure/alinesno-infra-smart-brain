package com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.service;


import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.PrototypeSceneEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IPrototypeSceneService extends IBaseService<PrototypeSceneEntity> {

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
    PrototypeSceneEntity getBySceneId(long sceneId, PermissionQuery query);


}
