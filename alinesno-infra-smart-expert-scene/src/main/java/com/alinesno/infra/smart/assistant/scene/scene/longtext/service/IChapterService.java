package com.alinesno.infra.smart.assistant.scene.scene.longtext.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.InitAgentsDto;
import com.alinesno.infra.smart.scene.dto.ChatContentEditDto;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import com.alinesno.infra.smart.scene.entity.LongTextTaskEntity;

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
     * @param chapters           章节列表，包含章节信息
     * @param parentId           父章节的ID，用于建立章节间的层级关系
     * @param level              章节的层级，表示章节在层级结构中的深度
     * @param sceneId
     * @param query
     * @param longTextTaskEntity
     */
    void saveChaptersWithHierarchy(List<TreeNodeDto> chapters, Long parentId, int level, long sceneId , long longTextSceneId, PermissionQuery query, LongTextTaskEntity longTextTaskEntity);

    /**
     * 获取章节的树形结构
     *
     * @return 返回一个表示章节层级结构的树形列表，每个节点包含章节信息
     */
    List<TreeNodeDto> getChapterTree(long sceneId, long longTextSceneId, Long taskId);

    /**
     * 更新章节的编辑者
     *
     * @param dto
     * @param longTextSceneId
     * @param taskId
     */
    void updateChapterEditor(ChatContentEditDto dto, Long longTextSceneId, Long taskId);

    /**
     * 初始化智能助手
     * @param dto
     */
    @Deprecated
    void initAgents(InitAgentsDto dto);

    /**
     * 更新智能助手
     * @param dto
     */
    void updateSceneAgents(UpdateSceneAgentDto dto);

    /**
     * 获取智能助手角色列表
     * @param dto
     * @return
     */
    @Deprecated
    List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto);

    /**
     * 获取所有章节内容
     * @param sceneId
     * @return
     */
    String getAllChapterContent(long sceneId);

    /**
     * 获取智能助手任务列表
     * @param taskId
     * @return
     */
    List<ChapterEntity> getChaptersByTaskId(Long taskId);
}
