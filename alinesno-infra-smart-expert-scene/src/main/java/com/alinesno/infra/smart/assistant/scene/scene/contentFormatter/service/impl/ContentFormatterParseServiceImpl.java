package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterParseMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterParseService;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterParseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ContentFormatterParseServiceImpl extends IBaseServiceImpl<ContentFormatterParseEntity , ContentFormatterParseMapper> implements IContentFormatterParseService {

    @Override
    public List<TreeNodeDto> getPlanTree(Long id, Long id1) {
        return null;
    }

    @Override
    public void saveChaptersWithHierarchy(List<TreeNodeDto> chapters, Long parentId, int level, long sceneId , long longTextSceneId) {

        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        Map<Integer, Integer> levelNumbers = new HashMap<>();
        for (int i = 1; i <= level; i++) {
            levelNumbers.put(i, 0); // 初始化每个层级的编号为0
        }

        saveChaptersWithHierarchyHelper(chapters, parentId, level, sceneId, longTextSceneId , levelNumbers);
    }

    private void saveChaptersWithHierarchyHelper(List<TreeNodeDto> chapters, Long parentId, int level, long sceneId, long longTextSceneId , Map<Integer, Integer> levelNumbers) {
        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        for (TreeNodeDto chapter : chapters) {

            ContentFormatterParseEntity entity = new ContentFormatterParseEntity();

            entity.setId(chapter.getId());
            entity.setParentChapterId(parentId);
            entity.setFormatterLevel(level);
            entity.setSceneId(sceneId);
            entity.setContentFormattingSceneId(longTextSceneId);
            entity.setIsLeaf(chapter.getChildren() == null || chapter.getChildren().isEmpty());

            // 生成标题
            String title = chapter.getLabel();
            entity.setFormatterName(title);
            entity.setFormatterRequire(chapter.getDescription());

            this.saveOrUpdate(entity);

            // 递归保存子章节
            if (chapter.getChildren() != null && !chapter.getChildren().isEmpty()) {
                levelNumbers.put(level, levelNumbers.getOrDefault(level, 0) + 1); // 当前层级编号递增
                saveChaptersWithHierarchyHelper(chapter.getChildren(), entity.getId(), level + 1, sceneId, longTextSceneId , levelNumbers);
            }

            levelNumbers.put(level, levelNumbers.getOrDefault(level, 0) + 1); // 当前层级编号递增
        }
    }
}