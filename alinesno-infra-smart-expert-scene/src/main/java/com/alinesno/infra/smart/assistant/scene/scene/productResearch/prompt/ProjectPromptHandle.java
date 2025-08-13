package com.alinesno.infra.smart.assistant.scene.scene.productResearch.prompt;


import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectSearchDTO;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeService;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * Prompt
 */
public class ProjectPromptHandle {

   private final static String PROMPT_DESIGN_OUTLINE_ZH = """
           ##角色：你是一个专业的大纲创作智能体，非常善于针对大纲领域，创作一个解决用户问题的大纲，并且对大纲中每一部分的执行要求给出具体指导，
               你善于理解用户的问题，专注于高层次研究策略制定、规划、高效分配子代理任务，根据输入的主题和信息和目前的知识库文档，制定有效的规划，
               生成结构化的解决分析方案指导大纲，用于给其它智能体角色做指导，大纲内不需要过于具体的数据
               
           ## 规划思路
           1. 问题评估与拆解
             - 识别任务中的核心概念、关键实体和关联关系
             - 列举需要获取的具体事实或数据点
             - 注意问题的时间或上下文限制
             - 分析用户最关注的核心要素及预期成果形式
             - 确定最终答案需要呈现的格式（详细报告/实体列表/多视角分析/可视化报告等）
             
           2. 问题类型判定
             - 深度优先型：需从多角度深入分析单一主题（如"抑郁症最有效疗法"）
             - 广度优先型：可拆分为独立子问题并行研究（如"比较三个北欧国家经济体系"）
             - 直接型：可通过单一调查回答的明确问题（如"东京当前人口"）
             
           3. 详细研究计划制定
             - 深度优先型：规划3-5种研究方法论/视角
             - 广度优先型：枚举可独立研究的子问题
             - 直接型：设计最直接的信息获取路径
             对每个步骤评估：可否拆分？需多视角？产出形式？必要性？
             
           ## 大纲拆分思路
           - 标准复杂度：拆分为4-5个部分（如"比较三大云服务商"）
           - 中高等复杂度：拆分为5-10个部分（如"AI对医疗的影响"）
           重要原则：避免超过10个拆分部分，优先使用能力更强的少量子代理
                
           ##规则：
            1-在创作大纲的时候，请你主要【附件资料】为主，调取你这个领域的专业数据进行创作大纲。
            2-在创作大纲的时候，请你平均拆分为N个部分，未来将有N个不同的智能体按照你的大纲进行创作具体的文字内容。请你保证N个部分边界清晰，没有重叠，避免冲突。
            3-在你创作的大纲的N个部分的时候，每个部分需要给出一个标题，然后给出一个具体的编写这部分内容编写要求，目的是给下一步拆分后的创作智能体参考。
            4-在你创作的N个部分编写要求中，除了对这部分的内容进行描述，也就是创作什么具体的内容。
            5-在编写要求中，要求要写详细明确，具备有指导性，请你根据用户的文字总数（文字总数不能超过1万个字）进行计算，对每一个部分创作具体内容时候要求的字数，给出要求，如果用户没有给出字数要求，每一部分给出合理的字数要求。
            6-最终请你下面的格式进行直接输出，不要输出其他信息，格式：
             ```json
            [
                {
                    "label": "第一部分大纲标题",
                    "description": "第一部分任务要求",
                    "children": [
                        {
                            "label": "第一部分大纲子标题1",
                            "description": "第一部分大纲子标题1任务要求"
                        },
                        {
                            "label": "第一部分大纲子标题2",
                            "description": "第一部分大纲子标题2任务要求"
                        },
                    ]
                },
                {
                    "label": "第二部分大纲标题",
                    "description": "第二部分任务要求"
                }
            ]
            ```
           ##需求=%s
           ##附件资料=%s
           """;

    /**
     * 生成项目跟进场景的提示
     * @param dto
     * @return
     */
    public static String generatorPrompt(ProjectSearchDTO dto) {

        IProjectKnowledgeService projectKnowledgeService = SpringUtils.getBean(IProjectKnowledgeService.class);
        List<ProjectKnowledgeEntity> projectKnowledgeList = projectKnowledgeService.list(new LambdaQueryWrapper<ProjectKnowledgeEntity>()
                .eq(ProjectKnowledgeEntity::getGroupId, dto.getDatasetGroupId())
        );

        // 获取到文件标题和内容的前100个字符
        List<String> titlesAndContent = projectKnowledgeList.stream()
                .map(knowledge -> knowledge.getDocName() + "：" + knowledge.getDocContent().substring(0, Math.min(knowledge.getDocContent().length(), 100)))
                .toList();

        // 将文件和内容拼接起来字符
        StringBuilder sb = new StringBuilder();
        for (String titleAndContent : titlesAndContent) {
            sb.append(titleAndContent).append("\n\n");
        }


        return String.format(PROMPT_DESIGN_OUTLINE_ZH , sb, dto.getPromptText()) ;
    }

    /**
     * 生成项目执行场景的提示
     * @param goal
     * @param outline
     * @param taskLabel
     * @param taskDesc
     * @return
     */
    public static String generatorExecutePrompt(String goal , String outline , String taskLabel , String taskDesc) {

        String formatContent = """
         ##目标=%s
         ##任务
         你要编写以下章节当中的指定内容，请联系上下文进行编写:
         ## 所有章节
         %s
         ## 需要编写的章节
         %s:%s
         """ ;

        return String.format(formatContent ,
                goal ,
                outline ,
                taskLabel ,
                taskDesc) ;
    }
}
