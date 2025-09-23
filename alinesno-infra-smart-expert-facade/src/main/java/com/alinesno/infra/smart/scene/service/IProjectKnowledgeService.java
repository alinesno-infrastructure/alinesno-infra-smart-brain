package com.alinesno.infra.smart.scene.service;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.embedding.EmbeddingOptions;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;

public interface IProjectKnowledgeService extends IBaseService<ProjectKnowledgeEntity> {

    /**
     * 导入文件数据
     *
     * @param entity
     * @param embeddingOptions
     */
    void importData(ProjectKnowledgeEntity entity , Llm llm, EmbeddingOptions embeddingOptions);

}