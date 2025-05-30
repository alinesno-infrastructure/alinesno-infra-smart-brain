package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.prompt;

import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGeneratorDTO;

public class ArticlePromptHandle {

    public static String generatorPrompt(ArticleGeneratorDTO dto) {
        String str = ArticlePrompt.PROMPT_AIPPT_OUTLINE_ZH +
                ArticlePrompt.PROMPT_AIPPT_OUTLINE_EXAMPLE_ZH  ;

        String chatPrompt = """
                ## 生成PPT需求
                生成PPT需求：%s
                生成页数: %s
                受众群体: %s
                PPT场景: %s
                语句表达: %s

                ##限制:
                - 只返回大纲内容，其它不相关内容不要返回
                """;

        String chatPromptText = String.format(chatPrompt,
                dto.getPromptText() ,
                dto.getPptConfig().getPages(),
                dto.getPptConfig().getAudience(),
                dto.getPptConfig().getScenario(),
                dto.getPptConfig().getTone());

        return str + chatPromptText;
    }

//    /**
//     * 创建PPT
//     *
//     * @param playload
//     * @param pptManager
//     * @return
//     */
//    public static String generatorPromptForGenerator(AIPPTRequestDto playload, PPTManagerEntity pptManager) {
//
//        String outline = pptManager.getOutlineList() ;
//
//        int chapters = countPatternOccurrences(outline, "^##\\s+第\\d+章");
//        int sections = countPatternOccurrences(outline, "^###\\s+第\\d+节");
//        int points = countPatternOccurrences(outline, "^-\\s+");
//
//        String str = String.format(ArticlePrompt.PROMPT_AIPPT_ZH , chapters, sections , points) +
//                ArticlePrompt.PROMPT_AIPPT_EXAMPLE_ZH  ;
//
//        System.out.println("章的数量：" + chapters);
//        System.out.println("节的数量：" + sections);
//        System.out.println("小点的数量：" + points);
//
//        String chatPrompt = """
//                ## PPT大纲
//                %s
//
//                ## 场景说明
//                生成页数: %s
//                受众群体: %s
//                PPT场景: %s
//                语句表达: %s
//
//                ##限制:
//                - 严格返回JSONL（JSON Lines）格式的数据，每个对象单独成行，无需额外解释
//                """;
//
//        ArticleConfigDto pptConfig = JSONObject.parseObject(pptManager.getPptConfig() , ArticleConfigDto.class) ;
//        String chatPromptText = String.format(chatPrompt,
//                outline ,
//                pptConfig.getPages(),
//                pptConfig.getAudience(),
//                pptConfig.getScenario(),
//                pptConfig.getTone());
//
//        return str + chatPromptText ;
//    }
//
//        private static int countPatternOccurrences(String text, String regex) {
//            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
//            Matcher matcher = pattern.matcher(text);
//            int count = 0;
//            while (matcher.find()) {
//                count++;
//            }
//            return count;
//    }

}
