package com.alinesno.infra.smart.assistant.scene.scene.longtext.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.LongTextSceneEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface ILongTextSceneService extends IBaseService<LongTextSceneEntity> {

    /**
     * 根据场景ID获取场景信息
     *
     * @param id
     * @param query
     * @return
     */
    LongTextSceneEntity getBySceneId(long id, PermissionQuery query);

    /**
     * 更新场景的AI代理信息
     * @param dto
     */
    void updateSceneAgents(UpdateSceneAgentDto dto);

    /**
     * 获取场景的AI代理信息
     * @param dto
     * @return
     */
    List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto);

}
