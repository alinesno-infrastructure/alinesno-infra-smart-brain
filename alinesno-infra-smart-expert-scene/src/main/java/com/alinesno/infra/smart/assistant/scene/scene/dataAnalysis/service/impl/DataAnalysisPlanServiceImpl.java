package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.mapper.DataAnalysisPlanMapper;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.IDataAnalysisPlanService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterParseEntity;
import com.alinesno.infra.smart.scene.entity.DataAnalysisPlanEntity;
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
public class DataAnalysisPlanServiceImpl extends IBaseServiceImpl<DataAnalysisPlanEntity, DataAnalysisPlanMapper> implements IDataAnalysisPlanService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Override
    public List<TreeNodeDto> getPlanTree(Long sceneId, Long analysisSceneId) {
        log.info("getChapterTree sceneId:{}", sceneId);

        LambdaUpdateWrapper<DataAnalysisPlanEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DataAnalysisPlanEntity::getSceneId, sceneId);
        wrapper.eq(DataAnalysisPlanEntity::getDataAnalysisSceneId, analysisSceneId);

        List<DataAnalysisPlanEntity> allChapters = this.list(wrapper);
        List<TreeNodeDto> treeNodes = new ArrayList<>();

        buildTree(allChapters, null, treeNodes);

        return treeNodes;
    }

    private void buildTree(List<DataAnalysisPlanEntity> allChapters, Long parentId, List<TreeNodeDto> treeNodes) {
        if (allChapters == null || allChapters.isEmpty()) {
            return;
        }

        for (DataAnalysisPlanEntity chapter : allChapters) {
            if (Objects.equals(chapter.getParentPlanId(), parentId)) {
                TreeNodeDto node = new TreeNodeDto();
                node.setId(chapter.getId());
                node.setLabel(chapter.getPlanName());
                node.setDescription(chapter.getPlanRequire()); // 或者使用其他字段作为描述

                // 获取到用户的id
                Long roleId = chapter.getDataMinerEngineerId() ;
                if(roleId != null){
                    IndustryRoleEntity role = roleService.getById(roleId) ;
                    node.setChapterEditorAvatar(role.getRoleAvatar());
                    node.setChapterEditor(roleId);
                }

                // 递归查找子节点
                node.setChildren(new ArrayList<>());
                buildTree(allChapters, chapter.getId(), node.getChildren());

                // 将构建好的节点添加到结果集中
                treeNodes.add(node);
            }
        }
    }

    @Override
    public void saveChaptersWithHierarchy(List<TreeNodeDto> chapters, Long parentId, int level, long sceneId , long dataAnalysisPlanId) {

        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        Map<Integer, Integer> levelNumbers = new HashMap<>();
        for (int i = 1; i <= level; i++) {
            levelNumbers.put(i, 0); // 初始化每个层级的编号为0
        }

        saveChaptersWithHierarchyHelper(chapters, parentId, level, sceneId, dataAnalysisPlanId , levelNumbers);
    }

    private void saveChaptersWithHierarchyHelper(List<TreeNodeDto> chapters, Long parentId, int level, long sceneId, long dataAnalysisPlanId , Map<Integer, Integer> levelNumbers) {
        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        for (TreeNodeDto chapter : chapters) {

            DataAnalysisPlanEntity entity = new DataAnalysisPlanEntity();

            entity.setId(chapter.getId());
            entity.setParentPlanId(parentId);
            entity.setPlanLevel(level);
            entity.setSceneId(sceneId);
            entity.setDataAnalysisSceneId(dataAnalysisPlanId);
            entity.setIsLeaf(chapter.getChildren() == null || chapter.getChildren().isEmpty());

            // 生成标题
            String title = chapter.getLabel();
            entity.setPlanName(title);
            entity.setPlanRequire(chapter.getDescription());

            this.saveOrUpdate(entity);

            // 递归保存子章节
            if (chapter.getChildren() != null && !chapter.getChildren().isEmpty()) {
                levelNumbers.put(level, levelNumbers.getOrDefault(level, 0) + 1); // 当前层级编号递增
                saveChaptersWithHierarchyHelper(chapter.getChildren(), entity.getId(), level + 1, sceneId, dataAnalysisPlanId , levelNumbers);
            }

            levelNumbers.put(level, levelNumbers.getOrDefault(level, 0) + 1); // 当前层级编号递增
        }
    }
}