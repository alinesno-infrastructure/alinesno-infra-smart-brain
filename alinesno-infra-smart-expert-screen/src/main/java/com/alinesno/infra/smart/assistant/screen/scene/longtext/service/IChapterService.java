package com.alinesno.infra.smart.assistant.screen.scene.longtext.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.screen.core.entity.ChapterEntity;
import com.alinesno.infra.smart.assistant.screen.core.dto.ChatContentEditDto;
import com.alinesno.infra.smart.assistant.screen.core.dto.TreeNodeDto;

import java.util.List;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IChapterService extends IBaseService<ChapterEntity> {

    /**
     * 根据层级结构保存章节及其父ID
     *
     * @param chapters 章节列表，包含章节信息
     * @param parentId 父章节的ID，用于建立章节间的层级关系
     * @param level    章节的层级，表示章节在层级结构中的深度
     * @param screenId
     */
    void saveChaptersWithHierarchy(List<TreeNodeDto> chapters, Long parentId, int level, long screenId);

    /**
     * 获取章节的树形结构
     *
     * @return 返回一个表示章节层级结构的树形列表，每个节点包含章节信息
     */
    List<TreeNodeDto> getChapterTree(long screenId);

    /**
     * 更新章节的编辑者
     * @param dto
     */
    void updateChapterEditor(ChatContentEditDto dto);

}
