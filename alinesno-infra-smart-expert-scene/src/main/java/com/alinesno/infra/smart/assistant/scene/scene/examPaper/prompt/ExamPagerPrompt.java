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


    public static final String EXAM_MARK_PROMPT_TEXT = """
            请对下面的题目和用户的回答进行阅卷，以下为考试试卷和用户回答的答案：
            %s
                
            ##任务
            - 针对于每一道题目进行打分，并写出打分的理由
            - 返回的结果需要格式化结果输出，并返回json格式方便应用解析:
                ```json
                [
                    {
                        "id":"问题的id" ,
                        "score": "评分的分数",
                        "maxScore": "题目满分" ,
                        "comment":"评分的理由和原因"
                    }
                ]
                ```
                比如:
                ```json
                [
                    {
                        "id": "1941514712164405249"
                        "score": 2,
                        "maxScore": 2 ,
                        "comment": "内容正确，符合培养要求，满分",
                    },
                    {
                        "id": "1941514712168599553"
                        "score": 1,
                        "maxScore": 2 ,
                        "comment": "选择不正确，答题不完善，1分",
                    },
                ]
            """ ;
}
