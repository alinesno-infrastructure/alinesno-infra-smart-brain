package com.alinesno.infra.smart.assistant.scene.scene.documentReader.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.dto.DocReaderInitDto;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DocReaderSceneEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IDocReaderSceneService extends IBaseService<DocReaderSceneEntity> {

    /**
     * 根据场景ID获取场景信息
     * @param sceneId
     * @return
     */
    DocReaderSceneEntity getBySceneId(long sceneId);

    /**
     * 初始化场景代理
     * @param dto
     */
    void initAgents(DocReaderInitDto dto);

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
}
