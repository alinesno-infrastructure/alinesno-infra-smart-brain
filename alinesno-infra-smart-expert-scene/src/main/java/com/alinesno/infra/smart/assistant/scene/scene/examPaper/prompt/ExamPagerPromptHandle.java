package com.alinesno.infra.smart.assistant.scene.scene.examPaper.prompt;

import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamStructureItem;

/**
 * 考试试卷生成提示处理类
 */
public class ExamPagerPromptHandle {

    /**
     * 生成考试试卷生成提示
     * @param chatContent
     * @param difficultyLevel
     * @param item
     * @return
     */
    public static String generatorPrompt(String chatContent  , String difficultyLevel , ExamStructureItem item){
        return String.format(ExamPagerPrompt.EXAM_PAPER_PROMPT_TEXT ,
                chatContent ,
                difficultyLevel ,
                item.getType() + ":" + item.getTypeName() + ":" + item.getTypeDesc() ,
                item.getTotalQuestion() ,
                item.getScorePerQuestion()) ;
    }

    /**
     * 生成考试试卷阅卷提示
     * @param markdownContent
     * @return
     */
    public static String generatorMarkPrompt(String markdownContent){
        return String.format(ExamPagerPrompt.EXAM_MARK_PROMPT_TEXT , markdownContent) ;
    }

}
