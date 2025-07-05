package com.alinesno.infra.smart.assistant.scene.scene.examPaper.prompt;

public class ExamPagerPrompt {

    /**
     * 生成试卷的提示文本
     */

    public static final String EXAM_PAPER_PROMPT_TEXT = """
           %s，
           题目难度:%s
           生成题目类型:%s，题目数量:%s，每道题目的分值是:%s
           
           生成每个题目包含以下的内容:
           题目/答案/正确答案/考核内容/答案解析/每题的分数
           
           要求:
           1. 考核解析的内容要详细，答案解析也要详细易懂，便于学员和教师更好的学习
           2. 完整的题目内容，用于生成试卷题目
           """ ;


}
