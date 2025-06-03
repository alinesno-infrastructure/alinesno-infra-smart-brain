package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.prompt;

import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGeneratorDTO;
import com.alinesno.infra.smart.scene.entity.ArticleTemplateEntity;

public class ArticlePromptHandle {

    public static String generatorPrompt(ArticleGeneratorDTO dto, ArticleTemplateEntity templateEntity) {

        String templatePrompt = "";

        if (templateEntity != null) {
            templatePrompt = String.format("""
                            %s
                            ##限制:
                            - 只返回文章内容，其它不相关内容不要返回
                            - 严格按照模板格式返回
                             """,
                    templateEntity.getPrompt()
            );
        }

        String chatPrompt = """
                ## 其它说明
                写作类型=%s
                写作类型=%s
                受众群体=%s
                文章场景=%s
                语句表达=%s
                """;

        String chatPromptText = String.format(chatPrompt,
                dto.getPromptText(),
                dto.getPptConfig().getWritingType(),
                dto.getPptConfig().getAudience(),
                dto.getPptConfig().getScenario(),
                dto.getPptConfig().getTone());

        return templatePrompt + chatPromptText;
    }

}
