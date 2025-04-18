package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterParseEntity;

import java.util.List;

public interface IContentFormatterParseService extends IBaseService<ContentFormatterParseEntity> {

    /**
     * 获取计划树
     * @param id
     * @param id1
     * @return
     */
    List<TreeNodeDto> getPlanTree(Long id, Long id1);

    /**
     * 根据层级结构保存章节及其父ID
     *
     * @param chapters 章节列表，包含章节信息
     * @param parentId 父章节的ID，用于建立章节间的层级关系
     * @param level    章节的层级，表示章节在层级结构中的深度
     * @param sceneId
     */
    void saveChaptersWithHierarchy(List<TreeNodeDto> chapters, Long parentId, int level, long sceneId , long longTextSceneId);
}