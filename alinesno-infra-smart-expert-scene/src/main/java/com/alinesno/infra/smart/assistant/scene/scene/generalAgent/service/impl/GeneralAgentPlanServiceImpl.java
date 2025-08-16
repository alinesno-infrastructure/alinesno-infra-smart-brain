package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.mapper.GeneralAgentPlanMapper;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentPlanService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.ChatContentEditDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.GeneralAgentPlanEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTaskEntity;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据分析规划服务实现类
 */
@Slf4j
@Service
public class GeneralAgentPlanServiceImpl extends IBaseServiceImpl<GeneralAgentPlanEntity, GeneralAgentPlanMapper> implements IGeneralAgentPlanService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Override
    public List<TreeNodeDto> getPlanTree(Long taskId, Long generalAgentPlanId) {
        log.info("getChapterTree taskId:{}", taskId);

        LambdaUpdateWrapper<GeneralAgentPlanEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(GeneralAgentPlanEntity::getTaskId, taskId);

        List<GeneralAgentPlanEntity> allChapters = this.list(wrapper);
        List<TreeNodeDto> treeNodes = new ArrayList<>();

        buildTree(allChapters, null, treeNodes);

        return treeNodes;
    }

    private void buildTree(List<GeneralAgentPlanEntity> allChapters, Long parentId, List<TreeNodeDto> treeNodes) {
        if (allChapters == null || allChapters.isEmpty()) {
            return;
        }

        for (GeneralAgentPlanEntity chapter : allChapters) {
            if (Objects.equals(chapter.getParentPlanId(), parentId)) {
                TreeNodeDto node = new TreeNodeDto();
                node.setId(chapter.getId());
                node.setLabel(chapter.getPlanName());
                node.setDescription(chapter.getPlanRequire()); // 或者使用其他字段作为描述

                // 获取到用户的id
                Long roleId = chapter.getBusinessExecutorEngineerId() ;
                if(roleId != null){
                    IndustryRoleEntity role = roleService.getById(roleId) ;
                    node.setChapterEditorAvatar(role.getRoleAvatar());
                    node.setChapterEditor(roleId);
                }

                // 设置是否有内容
                node.setHasContent(chapter.getContent() != null && !chapter.getContent().isEmpty());

                // 递归查找子节点
                node.setChildren(new ArrayList<>());
                buildTree(allChapters, chapter.getId(), node.getChildren());

                // 将构建好的节点添加到结果集中
                treeNodes.add(node);
            }
        }
    }

    @Override
    public void saveChaptersWithHierarchy(List<TreeNodeDto> chapters,
                                          Long parentId,
                                          int level,
                                          long sceneId ,
                                          long taskId ,
                                          long generalAgentPlanId) {

        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        Map<Integer, Integer> levelNumbers = new HashMap<>();
        for (int i = 1; i <= level; i++) {
            levelNumbers.put(i, 0); // 初始化每个层级的编号为0
        }

        saveChaptersWithHierarchyHelper(chapters, parentId, level, sceneId, taskId , generalAgentPlanId , levelNumbers);
    }

    @Override
    public void updateChapterEditor(ChatContentEditDto dto, Long generalAgentSceneId, Long taskId) {

        // generalAgentSceneId
        // 删除旧的编辑记录，重新添加新的编辑记录
        LambdaUpdateWrapper<GeneralAgentPlanEntity> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.eq(GeneralAgentPlanEntity::getSceneId, dto.getSceneId());
        updateWrapper.eq(GeneralAgentPlanEntity::getTaskId, taskId);
         updateWrapper.eq(GeneralAgentPlanEntity::getGeneralAgentSceneId, generalAgentSceneId);

        updateWrapper.eq(GeneralAgentPlanEntity::getBusinessExecutorEngineerId, dto.getRoleId());
        updateWrapper.set(GeneralAgentPlanEntity::getBusinessExecutorEngineerId, null) ;
        update(updateWrapper) ;

        // 重新设置编辑者
        if(dto.getChapters() != null && !dto.getChapters().isEmpty()){
            List<GeneralAgentPlanEntity> chapterList = listByIds(dto.getChapters());
            for (GeneralAgentPlanEntity chapter : chapterList) {
                chapter.setBusinessExecutorEngineerId(dto.getRoleId());
            }

            updateBatchById(chapterList);
        }

        // 更新taskGenStatus
        IGeneralAgentTaskService taskService = SpringUtils.getBean(IGeneralAgentTaskService.class);
        GeneralAgentTaskEntity task = taskService.getById(taskId);
        task.setGenContentStatus(TaskStatusEnum.RUN_COMPLETED.getCode());
        taskService.updateById(task);
    }

    private void saveChaptersWithHierarchyHelper(List<TreeNodeDto> chapters,
                                                 Long parentId,
                                                 int level,
                                                 long sceneId,
                                                 long taskId ,
                                                 long generalAgentPlanId ,
                                                 Map<Integer, Integer> levelNumbers) {
        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        for (TreeNodeDto chapter : chapters) {

            GeneralAgentPlanEntity entity = new GeneralAgentPlanEntity();

            entity.setId(chapter.getId());
            entity.setParentPlanId(parentId);
            entity.setPlanLevel(level);
            entity.setSceneId(sceneId);
            entity.setTaskId(taskId);
            entity.setGeneralAgentSceneId(generalAgentPlanId);
            entity.setIsLeaf(chapter.getChildren() == null || chapter.getChildren().isEmpty());

            // 生成标题
            String title = chapter.getLabel();
            entity.setPlanName(title);
            entity.setPlanRequire(chapter.getDescription());

            this.saveOrUpdate(entity);

            // 递归保存子章节
            if (chapter.getChildren() != null && !chapter.getChildren().isEmpty()) {
                levelNumbers.put(level, levelNumbers.getOrDefault(level, 0) + 1); // 当前层级编号递增
                saveChaptersWithHierarchyHelper(chapter.getChildren(),
                        entity.getId(),
                        level + 1,
                        sceneId,
                        taskId ,
                        generalAgentPlanId ,
                        levelNumbers);
            }

            levelNumbers.put(level, levelNumbers.getOrDefault(level, 0) + 1); // 当前层级编号递增
        }
    }

    @Override
    public String getAllChapterContent(long sceneId) {
        LambdaQueryWrapper<GeneralAgentPlanEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GeneralAgentPlanEntity::getSceneId, sceneId);
        lambdaQueryWrapper.orderByAsc(GeneralAgentPlanEntity::getPlanSort); // 按照排序字段升序排列

        List<GeneralAgentPlanEntity> allChapters = list(lambdaQueryWrapper);

        // 构建树结构
        Map<Long, GeneralAgentPlanEntity> chapterMap = new HashMap<>();
        List<GeneralAgentPlanEntity> topChapters = new ArrayList<>();

        for (GeneralAgentPlanEntity chapter : allChapters) {
            chapterMap.put(chapter.getId(), chapter);
        }

        for (GeneralAgentPlanEntity chapter : allChapters) {
            Long parentId = chapter.getParentPlanId();
            if (parentId == null) {
                topChapters.add(chapter);
            } else {
                GeneralAgentPlanEntity parent = chapterMap.get(parentId);
                if (parent != null) {
                    if (parent.getSubtitles() == null) {
                        parent.setSubtitles(new ArrayList<>());
                    }
                    parent.getSubtitles().add(chapter);
                }
            }
        }

        StringBuilder contentBuilder = new StringBuilder();

        for (GeneralAgentPlanEntity chapter : topChapters) {
            buildChapterContent(chapter, contentBuilder, 0);
        }

        return contentBuilder.toString();
    }

    @Override
    public List<GeneralAgentPlanEntity> getChaptersByTaskId(Long taskId) {
        LambdaQueryWrapper<GeneralAgentPlanEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeneralAgentPlanEntity::getTaskId, taskId);
        return this.list(wrapper);
    }

    private void buildChapterContent(GeneralAgentPlanEntity chapter, StringBuilder contentBuilder, int level) {
        // 根据层级添加缩进
        String indent = "  ".repeat(level);
        contentBuilder.append(indent).append("章节名称: ").append(chapter.getPlanName()).append("\n");
        if (chapter.getPlanRequire() != null) {
            contentBuilder.append(indent).append("编写要求: ").append(chapter.getPlanRequire()).append("\n");
        }
        if (chapter.getPlanAdditionalRequire() != null) {
            contentBuilder.append(indent).append("附加要求: ").append(chapter.getPlanAdditionalRequire()).append("\n");
        }
        contentBuilder.append("\n");

        if (chapter.getSubtitles() != null) {
            for (GeneralAgentPlanEntity subChapter : chapter.getSubtitles()) {
                buildChapterContent(subChapter, contentBuilder, level + 1);
            }
        }
    }
}