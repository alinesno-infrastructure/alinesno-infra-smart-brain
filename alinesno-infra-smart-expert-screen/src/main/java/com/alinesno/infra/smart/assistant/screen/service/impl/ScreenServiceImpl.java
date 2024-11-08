package com.alinesno.infra.smart.assistant.screen.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.screen.dto.ScreenDto;
import com.alinesno.infra.smart.assistant.screen.entity.ChapterEntity;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;
import com.alinesno.infra.smart.assistant.screen.mapper.ScreenMapper;
import com.alinesno.infra.smart.assistant.screen.service.IChapterService;
import com.alinesno.infra.smart.assistant.screen.service.IScreenService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ScreenServiceImpl extends IBaseServiceImpl<ScreenEntity, ScreenMapper> implements IScreenService {

    @Autowired
    private BaseSearchConsumer baseSearchConsumer; ;

    @Autowired
    private IChapterService chapterService ;

    @Override
    public ScreenEntity saveScreen(ScreenDto screenDto) {

        ScreenEntity screenEntity = new ScreenEntity();
        BeanUtils.copyProperties(screenDto, screenEntity);

        // 创建频道知识库
        R<String> createDataset = baseSearchConsumer.datasetCreate(
                screenEntity.getScreenDesc() ,
                screenEntity.getScreenName()) ;

        screenEntity.setKnowledgeId(createDataset.getData());
        save(screenEntity);

        return screenEntity ;
    }

    @Override
    public String genMarkdownContent(long screenId) {
        LambdaQueryWrapper<ChapterEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChapterEntity::getScreenId, screenId);
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
