package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.prompt;

import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.AIPPTRequestDto;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.PPTGeneratorDTO;
import com.alinesno.infra.smart.scene.entity.PPTManagerEntity;

public class AIPPTPromptHandle {

    public static String generatorPrompt(PPTGeneratorDTO dto) {
        String str = AiPPTPrompt.PROMPT_AIPPT_OUTLINE_ZH +
                AiPPTPrompt.PROMPT_AIPPT_OUTLINE_EXAMPLE_ZH  ;

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

    /**
     * 创建PPT
     *
     * @param playload
     * @param pptManager
     * @return
     */
    public static String generatorPromptForGenerator(AIPPTRequestDto playload, PPTManagerEntity pptManager) {

        String str = AiPPTPrompt.PROMPT_AIPPT_ZH +
                AiPPTPrompt.PROMPT_AIPPT_EXAMPLE_ZH  ;

        String chatPrompt = """
                ## 创建PPT
                PPT大纲：%s
                
                ##限制:
                - 严格返回JSONL（JSON Lines）格式的数据，每个对象单独成行，无需额外解释
                """;

        String chatPromptText = String.format(chatPrompt,
                pptManager.getOutlineList()) ;

        return str + chatPromptText ;
    }
}
