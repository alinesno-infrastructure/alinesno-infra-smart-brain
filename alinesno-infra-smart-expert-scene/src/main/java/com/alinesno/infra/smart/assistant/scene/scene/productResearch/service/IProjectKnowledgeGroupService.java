package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectKnowledgeGroupDto;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;

import java.util.List;
import java.util.Map;

public interface IProjectKnowledgeGroupService extends IBaseService<ProjectKnowledgeGroupEntity> {

    /**
     * 查询当前所有规则组及当前规则组下的所有规则列表
     *
     * @param query
     * @param sceneId
     * @return
     */
    List<ProjectKnowledgeGroupDto> listGroup(PermissionQuery query, Long sceneId);

    /**
     * 删除分组之后，当中关联的规则也会被删除掉
     * @param id
     */
    void removeGroup(Long id);

    /**
     * 导入数据
     * @param result
     * @param query
     */
    void importExcelData(Map<String, List<Map<String, Object>>> result, PermissionQuery query);
}