package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.GeneralAgentSceneEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IGeneralAgentSceneService extends IBaseService<GeneralAgentSceneEntity> {

    /**
     * 根据场景ID获取场景信息
     * @param sceneId
     * @return
     */
    GeneralAgentSceneEntity getBySceneId(long sceneId , PermissionQuery query);

//    /**
//     * 初始化场景代理
//     * @param dto
//     */
//    void initAgents(GeneralAgentInitDto dto);

    /**
     * 更新场景代理
     * @param dto
     */
    void updateSceneAgents(UpdateSceneAgentDto dto);

    /**
     * 获取场景角色列表
     * @param dto
     * @return
     */
    List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto);

    /**
     * 生成Markdown内容
     * @param sceneId
     * @param query
     * @param generalAgentSceneId
     * @return
     */
    String genMarkdownContent(long sceneId, long taskId , PermissionQuery query, Long generalAgentSceneId);

}
