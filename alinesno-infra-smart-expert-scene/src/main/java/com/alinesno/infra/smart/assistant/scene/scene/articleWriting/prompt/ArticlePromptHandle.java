package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.prompt;

import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGeneratorDTO;

public class ArticlePromptHandle {

    public static String generatorPrompt(ArticleGeneratorDTO dto) {
        String str = ArticlePrompt.PROMPT_ARTICLE_ZH ;

        String chatPrompt = """
                ## 生成PPT需求
                写作类型：%s
                生成页数: %s
                受众群体: %s
                文章场景: %s
                语句表达: %s

                ##限制:
                - 只返回文章内容，其它不相关内容不要返回
                """;

        String chatPromptText = String.format(chatPrompt,
                dto.getPromptText() ,
                dto.getPptConfig().getWritingType(),
                dto.getPptConfig().getAudience(),
                dto.getPptConfig().getScenario(),
                dto.getPptConfig().getTone());

        return str + chatPromptText;
    }

}
