package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewInitDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewSceneInfoDto;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IDocReviewSceneService extends IBaseService<DocReviewSceneEntity> {

    /**
     * 初始化智能助手
     * @param dto
     */
    void initAgents(DocReviewInitDto dto);

    /**
     * 根据场景ID获取场景信息
     *
     * @param sceneId
     * @param query
     * @return
     */
    DocReviewSceneEntity getBySceneId(long sceneId, PermissionQuery query);

    /**
     * 更新场景的智能助手
     * @param dto
     */
    void updateSceneAgents(UpdateSceneAgentDto dto);

    /**
     * 获取场景下的智能助手角色列表
     * @param dto
     * @return
     */
    List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto);

    /**
     * 获取场景信息
     * @param sceneId
     * @param taskId
     * @param entity
     * @return
     */
    DocReviewSceneInfoDto getDocReviewSceneInfoDto(long sceneId , long taskId , SceneEntity entity);

    /**
     * 获取场景信息，并统计审核结果数量
     * @param sceneId
     * @param taskId
     * @param entity
     * @return
     */
    DocReviewSceneInfoDto getDocReviewSceneInfoDtoWithResultCount(long sceneId , long taskId, SceneEntity entity);

    /**
     * 生成Markdown的审核报告格式
     *
     * @param sceneId
     * @param query
     * @param id
     * @param taskId
     * @return
     */
    String genMarkdownReport(long sceneId, PermissionQuery query, Long id, long taskId);
}
