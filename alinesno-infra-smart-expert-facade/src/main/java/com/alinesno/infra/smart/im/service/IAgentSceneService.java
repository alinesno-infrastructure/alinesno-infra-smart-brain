package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;

import java.util.List;

public interface IAgentSceneService extends IBaseService<AgentSceneEntity> {

    /**
     * 为场景添加角色
     *
     * @param roleId
     * @param sceneId
     * @param agentTypeId
     * @param llmModelId
     * @param sceneScope
     * @param orgId
     */
    void addRoleToScene(Long roleId, long sceneId, long agentTypeId, long llmModelId, long imageModelId, String sceneScope, Long orgId);

    /**
     * 下线场景角色
     * @param roleId
     */
    void offlineScene(Long roleId);

    /**
     * 根据场景ID和智能助手类型获取场景角色
     *
     * @param sceneId
     * @param agentTypeId
     * @param orgId
     * @return
     */
    List<IndustryRoleEntity> getRoleBySceneIdAndAgentType(long sceneId, long agentTypeId , long orgId);

    /**
     * 根据角色ID和场景获取场景角色
     * @param roleId
     * @param sceneId
     * @return
     */
    AgentSceneEntity getByRoleAndScene(long roleId, long sceneId);
}
