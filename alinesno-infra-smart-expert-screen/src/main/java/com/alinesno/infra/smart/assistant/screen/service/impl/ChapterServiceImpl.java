package com.alinesno.infra.smart.assistant.screen.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.screen.dto.ChapterDto;
import com.alinesno.infra.smart.assistant.screen.dto.TreeNodeDto;
import com.alinesno.infra.smart.assistant.screen.entity.ChapterEntity;
import com.alinesno.infra.smart.assistant.screen.mapper.ChapterMapper;
import com.alinesno.infra.smart.assistant.screen.service.IChapterService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChapterServiceImpl extends IBaseServiceImpl<ChapterEntity, ChapterMapper> implements IChapterService {

    @Override
    public void saveChaptersWithHierarchy(List<ChapterDto> chapters, Long parentId, int level, long screenId) {
        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        for (ChapterDto chapter : chapters) {
            chapter.setParentChapterId(parentId);
            chapter.setChapterLevel(level);
            chapter.setScreenId(screenId);
            chapter.setIsLeaf(chapter.getSubtitles() == null || chapter.getSubtitles().isEmpty());

            // 保存当前章节
            ChapterEntity entity = new ChapterEntity();
            BeanUtils.copyProperties(chapter, entity);
            this.save(entity);

            // 递归保存子章节
            if (chapter.getSubtitles() != null && !chapter.getSubtitles().isEmpty()) {
                saveChaptersWithHierarchy(chapter.getSubtitles(), entity.getId(), level + 1, screenId);
            }
        }
    }

    @Override
    public List<TreeNodeDto> getChapterTree(long screenId) {
        log.info("getChapterTree screenId:{}", screenId);

        LambdaUpdateWrapper<ChapterEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChapterEntity::getScreenId, screenId);

        List<ChapterEntity> allChapters = this.list(wrapper);
        List<TreeNodeDto> treeNodes = new ArrayList<>();

        buildTree(allChapters, null, treeNodes);

        return treeNodes ;
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

                // 递归查找子节点
                node.setChildren(new ArrayList<>());
                buildTree(allChapters, chapter.getId(), node.getChildren());

                // 将构建好的节点添加到结果集中
                treeNodes.add(node);
            }
        }
    }
}
