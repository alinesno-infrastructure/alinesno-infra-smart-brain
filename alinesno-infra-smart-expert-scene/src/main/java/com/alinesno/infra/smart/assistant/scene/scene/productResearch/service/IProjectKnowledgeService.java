package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;

public interface IProjectKnowledgeService extends IBaseService<ProjectKnowledgeEntity> {

    /**
     * 导入文件数据
     * @param entity
     */
    void importData(ProjectKnowledgeEntity entity);

}