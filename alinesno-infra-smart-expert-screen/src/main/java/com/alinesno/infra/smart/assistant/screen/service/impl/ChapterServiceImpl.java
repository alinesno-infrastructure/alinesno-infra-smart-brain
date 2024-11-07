package com.alinesno.infra.smart.assistant.screen.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.screen.dto.ChatContentEditDto;
import com.alinesno.infra.smart.assistant.screen.dto.TreeNodeDto;
import com.alinesno.infra.smart.assistant.screen.entity.ChapterEntity;
import com.alinesno.infra.smart.assistant.screen.mapper.ChapterMapper;
import com.alinesno.infra.smart.assistant.screen.service.IChapterService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ChapterServiceImpl extends IBaseServiceImpl<ChapterEntity, ChapterMapper> implements IChapterService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Override
    public void saveChaptersWithHierarchy(List<TreeNodeDto> chapters, Long parentId, int level, long screenId) {

        // 先删除当前场景下的所有章节
        LambdaUpdateWrapper<ChapterEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChapterEntity::getScreenId, screenId);
        remove(wrapper);

        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        Map<Integer, Integer> levelNumbers = new HashMap<>();
        for (int i = 1; i <= level; i++) {
            levelNumbers.put(i, 0); // 初始化每个层级的编号为0
        }

        saveChaptersWithHierarchyHelper(chapters, parentId, level, screenId, levelNumbers);
    }

    private void saveChaptersWithHierarchyHelper(List<TreeNodeDto> chapters, Long parentId, int level, long screenId, Map<Integer, Integer> levelNumbers) {
        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        for (TreeNodeDto chapter : chapters) {

            ChapterEntity entity = new ChapterEntity();

            entity.setId(chapter.getId());
            entity.setParentChapterId(parentId);
            entity.setChapterLevel(level);
            entity.setScreenId(screenId);
            entity.setIsLeaf(chapter.getChildren() == null || chapter.getChildren().isEmpty());

            // 生成标题
//            String title = generateTitle(levelNumbers, level) + " " + chapter.getLabel();
            String title = chapter.getLabel();
            entity.setChapterName(title);
            entity.setChapterRequire(chapter.getDescription());

            this.saveOrUpdate(entity);

            // 递归保存子章节
            if (chapter.getChildren() != null && !chapter.getChildren().isEmpty()) {
                levelNumbers.put(level, levelNumbers.getOrDefault(level, 0) + 1); // 当前层级编号递增
                saveChaptersWithHierarchyHelper(chapter.getChildren(), entity.getId(), level + 1, screenId, levelNumbers);
            }

            levelNumbers.put(level, levelNumbers.getOrDefault(level, 0) + 1); // 当前层级编号递增
        }
    }

    // TODO 待优化章节标题
    private String generateTitle(Map<Integer, Integer> levelNumbers, int level) {
        StringBuilder titleBuilder = new StringBuilder();

        for (int i = 1; i <= level; i++) {
            int number = levelNumbers.getOrDefault(i, 0); // 防止null值
            if (i == 1) {
                titleBuilder.append("第").append(number + 1).append("章");
            } else {
                titleBuilder.append(".").append(number + 1);
            }
        }

        return titleBuilder.toString();
    }

    @Override
    public List<TreeNodeDto> getChapterTree(long screenId) {
        log.info("getChapterTree screenId:{}", screenId);

        LambdaUpdateWrapper<ChapterEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChapterEntity::getScreenId, screenId);

        List<ChapterEntity> allChapters = this.list(wrapper);
        List<TreeNodeDto> treeNodes = new ArrayList<>();

        buildTree(allChapters, null, treeNodes);

        return treeNodes;
    }

    @Override
    public void updateChapterEditor(ChatContentEditDto dto) {

        // 删除旧的编辑记录，重新添加新的编辑记录
        LambdaQueryWrapper<ChapterEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterEntity::getScreenId, dto.getScreenId());
        wrapper.eq(ChapterEntity::getChapterEditor, dto.getRoleId());
        List<ChapterEntity> oldChapterList = list(wrapper);
        for (ChapterEntity chapter : oldChapterList) {
            chapter.setChapterEditor(null);
        }
        updateBatchById(oldChapterList);

        // 重新设置编辑者
        List<ChapterEntity> chapterList = listByIds(dto.getChapters());
        for (ChapterEntity chapter : chapterList) {
            chapter.setChapterEditor(dto.getRoleId());
        }

        updateBatchById(chapterList);
    }

    private void buildTree(List<ChapterEntity> allChapters, Long parentId, List<TreeNodeDto> treeNodes) {
        if (allChapters == null || allChapters.isEmpty()) {
            return;
        }

        for (ChapterEntity chapter : allChapters) {
            if (Objects.equals(chapter.getParentChapterId(), parentId)) {
                TreeNodeDto node = new TreeNodeDto();
                node.setId(chapter.getId());
                node.setLabel(chapter.getChapterName());
                node.setDescription(chapter.getChapterRequire()); // 或者使用其他字段作为描述

                // 获取到用户的id
                Long roleId = chapter.getChapterEditor() ;
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
}
