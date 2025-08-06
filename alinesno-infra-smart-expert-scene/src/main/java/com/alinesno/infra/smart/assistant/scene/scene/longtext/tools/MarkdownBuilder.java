package com.alinesno.infra.smart.assistant.scene.scene.longtext.tools;

import com.alinesno.infra.smart.scene.entity.ChapterEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * MarkdownBuilder类
 */
public class MarkdownBuilder {

    @NotNull
    public static String toMarkdownText(List<ChapterEntity> topChapters) {
        StringBuilder markdownBuilder = new StringBuilder();

        for (ChapterEntity chapter : topChapters) {
            MarkdownBuilder.buildMarkdownForChapter(chapter, markdownBuilder, 1);
        }

        return markdownBuilder.toString();
    }


    /**
     * 构建Markdown
     * @param chapter
     * @param markdownBuilder
     * @param level
     */
    public static void buildMarkdownForChapter(ChapterEntity chapter, StringBuilder markdownBuilder, int level) {
        // 根据层级添加标题符号
        markdownBuilder.append("#".repeat(Math.max(0, level)));
        markdownBuilder.append(" ").append(chapter.getChapterName()).append("\n\n");
        if (chapter.getContent() != null) {
            markdownBuilder.append(chapter.getContent()).append("\n\n");
        }

        if (chapter.getSubtitles() != null) {
            for (ChapterEntity subChapter : chapter.getSubtitles()) {
                buildMarkdownForChapter(subChapter, markdownBuilder, level + 1);
            }
        }
    }

}
