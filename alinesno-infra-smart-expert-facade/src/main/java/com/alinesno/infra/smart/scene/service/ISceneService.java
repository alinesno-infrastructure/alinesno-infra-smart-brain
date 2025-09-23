package com.alinesno.infra.smart.scene.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.*;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface ISceneService extends IBaseService<SceneEntity> {

    /**
     * 保存一个SceneEntity对象
     * @param sceneDto
     * @return
     */
    SceneEntity saveScene(SceneDto sceneDto);

    /**
     * 生成markdown内容
     *
     * @param sceneId
     * @param query
     * @param id
     * @param taskId
     * @return
     */
    String genMarkdownContent(long sceneId, PermissionQuery query, Long id, Long taskId);

    /**
     * 分页查询场景列表
     * @param query
     * @param page
     * @return
     */
    IPage<SceneResponseDto> sceneListByPage(PermissionQuery query, SceneQueryDto page);

    /**
     * 查询所有公共场景
     * @return
     */
    List<SceneEntity> allPublicScene();

    /**
     *  更新不同的场景Agent角色
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
