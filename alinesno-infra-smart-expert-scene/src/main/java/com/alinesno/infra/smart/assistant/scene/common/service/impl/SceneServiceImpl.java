package com.alinesno.infra.smart.assistant.scene.common.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.scene.common.mapper.SceneMapper;
import com.alinesno.infra.smart.assistant.scene.common.service.ISceneService;
import com.alinesno.infra.smart.assistant.scene.core.entity.ChapterEntity;
import com.alinesno.infra.smart.assistant.scene.core.entity.SceneEntity;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SceneServiceImpl extends IBaseServiceImpl<SceneEntity, SceneMapper> implements ISceneService {

    @Autowired
    private BaseSearchConsumer baseSearchConsumer; ;

    @Autowired
    private IChapterService chapterService ;

    @Override
    public SceneEntity saveScene(SceneDto sceneDto) {

        SceneEntity sceneEntity = new SceneEntity();
        BeanUtils.copyProperties(sceneDto, sceneEntity);

        // 创建频道知识库
        R<Long> createDataset = baseSearchConsumer.datasetCreate(
                sceneEntity.getSceneDesc() ,
                sceneEntity.getSceneName() ,
                sceneDto.getOrgId() + "" ,
                sceneDto.getOperatorId() + "") ;

        sceneEntity.setKnowledgeId(createDataset.getData()+"");
        save(sceneEntity);

        return sceneEntity ;
    }

    @Override
    public String genMarkdownContent(long sceneId) {
        LambdaQueryWrapper<ChapterEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChapterEntity::getSceneId, sceneId);
        lambdaQueryWrapper.orderByAsc(ChapterEntity::getChapterSort); // 按照排序字段升序排列

        List<ChapterEntity> topChapters = chapterService.list(lambdaQueryWrapper);

        StringBuilder markdownBuilder = new StringBuilder();

        for (ChapterEntity chapter : topChapters) {
            buildMarkdownForChapter(chapter, markdownBuilder);
        }

        return markdownBuilder.toString();
    }

    private void buildMarkdownForChapter(ChapterEntity chapter, StringBuilder markdownBuilder) {
        // 根据章节层级生成对应的Markdown标题前缀
        String markdownHeaderPrefix = "#".repeat(chapter.getChapterLevel());
        // 拼接章节名称
        markdownBuilder.append(markdownHeaderPrefix).append(" ").append(chapter.getChapterName()).append("\n\n");
        // 如果有内容，则添加内容
        if (chapter.getContent() != null && !chapter.getContent().isEmpty()) {
            markdownBuilder.append(chapter.getContent()).append("\n\n");
        }

        // 递归处理子章节
        List<ChapterEntity> subtitles = chapter.getSubtitles();
        if (subtitles != null && !subtitles.isEmpty()) {
            for (ChapterEntity subtitle : subtitles) {
                buildMarkdownForChapter(subtitle, markdownBuilder);
            }
        }
    }
}
