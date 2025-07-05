package com.alinesno.infra.smart.assistant.scene.scene.examPaper.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ExamPagerEntity;
import com.alinesno.infra.smart.scene.entity.ExamPagerSceneEntity;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IExamPagerSceneService extends IBaseService<ExamPagerSceneEntity> {

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
    ExamPagerSceneEntity getBySceneId(long sceneId, PermissionQuery query);

}
