package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DataAnalysisSceneEntity;

import java.util.List;

/**
 * 数据分析场景服务接口
 */
public interface IDataAnalysisSceneService extends IBaseService<DataAnalysisSceneEntity> {

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
     * @param id
     * @param query
     * @return
     */
    DataAnalysisSceneEntity getBySceneId(long id, PermissionQuery query);
}