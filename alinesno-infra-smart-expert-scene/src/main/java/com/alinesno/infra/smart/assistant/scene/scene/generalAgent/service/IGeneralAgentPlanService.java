package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.dto.ChatContentEditDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.GeneralAgentPlanEntity;

import java.util.List;

/**
 * 数据分析规划服务接口
 */
public interface IGeneralAgentPlanService extends IBaseService<GeneralAgentPlanEntity> {

    /**
     * 获取章节树
     * @param taskId
     * @param analysisSceneId
     * @return
     */
    List<TreeNodeDto> getPlanTree(Long taskId, Long analysisSceneId);

    /**
     * 根据层级结构保存章节及其父ID
     *
     * @param chapters 章节列表，包含章节信息
     * @param parentId 父章节的ID，用于建立章节间的层级关系
     * @param level    章节的层级，表示章节在层级结构中的深度
     * @param sceneId
     */
    void saveChaptersWithHierarchy(List<TreeNodeDto> chapters, Long parentId, int level, long sceneId , long taskId , long generalAgentPlanId);

    /**
     * 更新章节编辑器
     *
     * @param dto
     * @param longTextSceneId
     * @param taskId
     */
    void updateChapterEditor(ChatContentEditDto dto, Long longTextSceneId , Long taskId) ; // generalAgentSceneId);

    /**
     * 获取场景所有章节内容
     * @param sceneId
     * @return
     */
    String getAllChapterContent(long sceneId);

    /**
     * 根据任务ID获取所有章节
     * @param taskId
     * @return
     */
    List<GeneralAgentPlanEntity> getChaptersByTaskId(Long taskId);
}