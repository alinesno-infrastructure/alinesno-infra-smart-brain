package com.alinesno.infra.smart.im.constants;

public interface AgentConstants {


    String STEP_START = "start" ; // 节点开始
    String STEP_PROCESS = "process" ; // 节点任务进行中
    String STEP_FINISH = "finish" ;  //  节点结束
    String EMPTY_RESULT = "" ;  //  空结果

    long ORG_AGENT_STORE_TYPE_ID = 9527L ;  // 角色类型ID
    long STORE_EMPTY_ORG_ID = 0L ;  // 商店组织为空


    interface Role {
        String LEADER = "leader";
        String WORKER = "worker";
    }

    interface ChatText {
        String CHAT_SUCCESS_FINISH = "success" ;
        String CHAT_FAIL_FINISH = "fail" ;
        String CHAT_FINISH = "表达结束" ;
        String CHAT_OPT_SUCCESS_FINISH = "操作成功" ;
        String CHAT_NO_ANSWER = "我尝试找了很多次，但是未找到答案" ;
        String CHAT_WAITING_WITH_TIME = "收到，任务我已经在处理，请稍等1-2分钟 :-)" ;
        String CHAT_WAITING_NOT_TIME = "收到，任务我已经在处理，请稍等 :-)" ;
    }

    interface HierarchicalManagerAgent {
        String ROLE = "团队经理";
        String GOAL = "以最佳方式管理团队完成任务。";
        String BACKSTORY = "你是一位经验丰富的经理，擅长激发团队的最大潜力。\n你以能够将工作委派给合适的人选，并提出正确的问题来激励团队而闻名。\n尽管你不亲自执行任务，但你在该领域拥有丰富的经验，这使你能够准确评估团队成员的工作。";
    }

    interface Slices {
        String LOOP_COUNT= "\n##第%s次思考执行:";
        String OBSERVATION = "\n观察";
        String THOUGHT = "\n场景的思考：%s \r\n";
        String EXECUTE_TOOL_RESULT = "工具执行结果: %s \r\n" ;
        String ANSWER_RESULT = "思考分析结果: %s \r\n" ;
        String PRE_CONTENT= "引用(%s)输出的内容:\n";
        String PRE_CONTENT_WITH_HEADER = "\n ##引用(%s)输出的内容:\n";
        String DATASET_CONTENT= "\n## 你的知识库内容:\n";
        String REFERENCE_CONTENT= "\n## 你引用附件内容:\n";
        String TASK = "\n## 当前任务: %s\n\n开始！这对您来说非常重要，请充分利用可用工具并给出您的最佳最终答案，您的工作取决于此！\n\n思考：";
        String MEMORY = "\n\n## 有用的上下文: \n%s";
        String ROLE_PLAYING = "你是 %s。%s\n你的个人目标是: %s";
        String TOOLS = "\n你仅能访问以下工具，绝不能编造未列出的工具：\n\n%s\n\n请使用以下格式：\n\n思考：你应始终考虑要做什么\n行动：采取的行动，只能是[%s]中的一个名称，仅名称，完全按照所写的形式。\n行动输入：行动的输入，只是一个简单的Python字典，用大括号包围，使用\"来包裹键和值。\n观察：行动的结果\n\n一旦收集到所有必要的信息：\n\n思考：我现在知道了最终答案\n最终答案：对原始输入问题的最佳最终答案\n";
        String NO_TOOLS = "为了给出对任务的最佳完整最终答案，请使用确切的以下格式：\n\n思考：我现在可以给出一个很好的答案\n最终答案：我对任务的最佳完整最终答案。\n你的最终答案必须尽可能伟大且完整，它必须描述结果。\n\n我必须使用这些格式，我的工作取决于此！";
        String FORMAT = "我必须要么使用一个工具（一次使用一个）要么给出我的最佳最终答案。使用以下格式：\n\n思考：你应始终考虑要做什么\n行动：采取的行动，应该是[%s]之一\n行动输入：行动的输入，字典用大括号包围\n观察：行动的结果\n... （这个思考/行动/行动输入/观察可以重复N次）\n思考：我现在可以给出一个很好的答案\n最终答案：我对任务的最佳完整最终答案。\n你的最终答案必须尽可能伟大且完整，它必须描述结果\n\n";
        String FINAL_ANSWER_FORMAT = "如果你不再需要使用任何工具，你必须给出你的最佳完整最终答案，确保它满足预期标准，使用下面的确切格式：\n\n思考：我现在可以给出一个很好的答案\n最终答案：我对任务的最佳完整最终答案。\n\n";
        String FORMAT_WITHOUT_TOOLS = "\n对不起，我没有使用正确的格式。我必须要么使用一个工具（从可用选项中选择），要么给出我的最佳最终答案。\n我只是记起了我必须遵循的预期格式：\n\n问题：你必须回答的输入问题\n思考：你应始终考虑要做什么\n行动：采取的行动，应该是[%s]之一\n行动输入：行动的输入\n观察：行动的结果\n... （这个思考/行动/行动输入/观察可以重复N次）\n思考：我现在可以给出一个很好的答案\n最终答案：我对任务的最佳完整最终答案\n你的最终答案必须尽可能伟大且完整，它必须描述结果\n\n";
        String TASK_WITH_CONTEXT = "%s\n\n这是你要工作的上下文：\n%s";
        String EXPECTED_OUTPUT = "\n这是对你最终答案的预期标准， \n你必须返回实际的完整内容作为最终答案，而不是摘要。";
        String HUMAN_FEEDBACK = "你收到了关于你工作的人员反馈，请重新评估并在准备好后给出新的最终答案。\n %s";
        String GETTING_INPUT = "这是代理的最终答案：%s\n请提供反馈：";
    }

    interface Errors {
        String FORCE_FINAL_ANSWER = "由于现在是时候给出你的最终答案，工具不会被使用。不要使用工具，只需给出你绝对最好的最终答案。";
        String AGENT_TOOL_UNEXISTING_COWORKER = "\n执行工具时出错。提到的同事未找到，它必须是以下选项之一：\n%s\n";
        String TASK_REPEATED_USAGE = "我尝试重用相同的输入，我必须停止使用这个动作输入。我会尝试其他方法。\n\n";
        String TOOL_USAGE_ERROR = "我遇到了错误：%s";
        String TOOL_ARGUMENTS_ERROR = "错误：行动输入不是一个有效的键值字典。";
        String WRONG_TOOL_NAME = "你试图使用工具 %s，但它不存在。你必须使用以下工具之一，一次使用一个：%s。";
        String TOOL_USAGE_EXCEPTION = "尝试使用工具时我遇到了错误。这是错误：%s。\n 工具 %s 接受这些输入：%s";
    }

    interface Tools {
        String DELEGATE_WORK = "将特定任务委托给以下同事之一：%s\n此工具的输入应该是同事的名字，你想让他们做的任务，以及执行任务所需的所有必要上下文，他们对任务一无所知，所以分享你所知道的一切，不要引用事物而是解释它们。";
        String ASK_QUESTION = "向以下同事之一询问具体问题：%s\n此工具的输入应该是同事的名字，你对他们的问题，以及正确提问所需的所有必要上下文，他们对问题一无所知，所以分享你所知道的一切，不要引用事物而是解释它们。";
    }
}