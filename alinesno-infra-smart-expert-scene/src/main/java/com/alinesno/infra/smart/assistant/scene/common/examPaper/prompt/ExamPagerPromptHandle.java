package com.alinesno.infra.smart.assistant.scene.common.examPaper.prompt;

import com.alinesno.infra.smart.assistant.scene.common.examPaper.dto.ExamStructureItem;

public class ExamPagerPromptHandle {

    public static String generatorPrompt(String chatContent  , String difficultyLevel , ExamStructureItem item){
        return String.format(ExamPagerPrompt.EXAM_PAPER_PROMPT_TEXT ,
                chatContent ,
                difficultyLevel ,
                item.getType() + ":" + item.getTypeName() + ":" + item.getTypeDesc() ,
                item.getTotalQuestion() ,
                item.getScorePerQuestion()) ;
    }

}
