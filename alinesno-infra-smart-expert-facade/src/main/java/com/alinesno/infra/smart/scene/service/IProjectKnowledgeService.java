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

    /**
     * 判断指定 group 下是否存在相同 md5 的记录（用于去重）
     */
    boolean existsByMd5AndGroupId(String md5, Long groupId);

    /**
     * 判断指定 group 是否存在处于“处理中”的导入（vector_status = 0）
     */
    boolean hasOngoingImport(Long groupId);

    /**
     * 重命名知识
     * @param id
     * @param newName
     */
    void renameKnowledge(Long id, String newName);

    /**
     * 删除知识
     * @param id
     * @throws Exception
     */
    void deleteKnowledgeById(Long id) throws Exception;

}