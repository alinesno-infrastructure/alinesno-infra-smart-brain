package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;

public interface IAgentSceneService extends IBaseService<AgentSceneEntity> {

    /**
     * 为场景添加角色
     * @param roleId
     * @param sceneId
     * @param sceneAgentId
     */
    void addRoleToScene(Long roleId, long sceneId, long sceneAgentId);

}
