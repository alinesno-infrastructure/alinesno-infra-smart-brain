package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt;

/**
 * PlanningPrompts
 */
public class PlanningPrompts {

    public static final String DEFAULT_PLANNING_MAKE_PROMPT = """
            你是一个任务规划专家，根据现有的知识库内容和能使用的工具列表，善于将复杂的问题拆解成详细的，可独立执行的任务列表
            ###需要处理的问题###
            ${complex_task}
            ###知识库###
            ${dataset_knowledge_info!""}
            ###工具列表###
            ${tool_info!""}
            限制1：你创建的计划任务数量最多为 ${max_plannings} 条
            限制2: 请列出你的执行计划列表，每一步的执行计划内容
            """;

    /**
     * 格式化的Prompt
     */
    public static final String DEFAULT_PLANNING_MAKE_PROMPT_FORMATTED = """
            你会收到一份任务执行规划，但是需要对数据进行JSON格式化输出，请整理成JSON格式化。
            规划内容:
            ${plan_content}
           
            限制:
            1. 按以下JSON格式返回，并严格返回任务安排的json数据格式
            3. 其它非相关性内容不要输出
            ```json
            [
                {
                    "order":"<任务执行顺序，比如1>",
                    "taskName":"<要执行的任务名称，比如市场调研报告撰写>" ,
                    "taskDesc":"<任务的详细描述，关键名词，细致详细，比如收集行业近三年市场数据，分析竞争对手动态、用户需求趋势及潜在机会，整理成PPT格式报告，要求包含至少5个竞品对比分析图表，下周五前提交部门评审。>"
                }
            ]
            ```
           """ ;

//    public static final String DEFAULT_PLANNING_UPDATE_PROMPT = """
//            你是一个项目管理专家，目前正在带领团队解决一个复杂问题：
//            ${complex_task}
//
//            团队成员列表：
//            ${worker_details}
//
//            这个复杂任务被拆解成了如下的执行计划，目前执行进度如下：
//            ${planning_details}
//
//            现在 ${worker_name} 刚刚完成了 任务 ${completed_task}，返回了如下执行报告：
//            ${completed_task_result}
//
//            请根据上面的情况调整更新计划，你可以进行如下四种操作：
//
//            1. 如果你认为此任务已经完成，可以调用 mark_task_done 标记任务完成
//            2. 如果你需要调整一个任务的描述，可以调用 update_task
//            3. 如果你需要增加新的任务，可以调用 add_task
//            4. 如果你觉得某个任务不需要再执行了，可以调用 delete_task
//            """;

}