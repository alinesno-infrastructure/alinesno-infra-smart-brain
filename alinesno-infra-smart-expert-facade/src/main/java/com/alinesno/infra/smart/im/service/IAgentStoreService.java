package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.common.facade.wrapper.RpcWrapper;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.entity.AgentStoreEntity;

import java.util.List;

public interface IAgentStoreService extends IBaseService<AgentStoreEntity> {

    /**
     * 添加角色到商店
     *
     * @param roleId
     * @param agentStoreTypeId
     * @param sceneScope
     * @param orgId
     */
    void addRoleToStore(Long roleId , Long agentStoreTypeId, String sceneScope, Long orgId);

    /**
     * 从商店中查询角色信息
     */
    List<IndustryRoleEntity> findRoleFromStore(DatatablesPageBean page);

    /**
     * 从商店中查询组织角色信息
     */
    List<IndustryRoleEntity> findRoleFromStoreByOrgId(DatatablesPageBean page , long orgId);

    /**
     * 通过场景指定Agent类型，查询角色信息
     */
    List<IndustryRoleEntity> findRoleBySceneAgentId( DatatablesPageBean page , long sceneAgentId);

    /**
     * 通过场景查询角色
     */
    List<IndustryRoleEntity> findRoleBySceneId(DatatablesPageBean page , long sceneId);

    /**
     * 推送角色到指定组织的商店
     * @param roleId
     * @param orgId
     */
    void pushOrgRole(long roleId, long orgId);

    /**
     * 角色下线商店
     * @param roleId
     */
    void offlineStore(Long roleId);

    /**
     * 查询出所有商店的角色（包括公开商店和当前组织商店的）
     * @param page
     * @param orgId
     * @return
     */
    TableDataInfo storeRoleList(DatatablesPageBean page, Long orgId);

    /**
     * 获取商店角色并且是用户推荐状态的角色
     * @return
     */
    IndustryRoleEntity getRecommendRole();
}
