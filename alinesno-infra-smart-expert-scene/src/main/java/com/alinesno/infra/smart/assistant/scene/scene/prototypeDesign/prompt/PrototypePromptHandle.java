package com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.prompt;

import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGeneratorDTO;

/**
 * PrototypePromptHandle
 */
public class PrototypePromptHandle {

    public static String generatorPrompt(ArticleGeneratorDTO dto , String designTemplate) {
        String str = String.format(PrototypePrompt.PROMPT_ARTICLE_ZH ,designTemplate)  ;

        String chatPrompt = """
                ## 生成原型需求
                用户需求=%s
                """;

        String chatPromptText = String.format(chatPrompt,
                dto.getPromptText()) ;
//                dto.getPptConfig().getWritingType(),
//                dto.getPptConfig().getAudience(),
//                dto.getPptConfig().getScenario(),
//                dto.getPptConfig().getTone());

        return str + chatPromptText;
    }

}
