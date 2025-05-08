package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt;

/**
 * 总结的Prompt命令
 */
public class SummaryPrompt {

    public static final String DEFAULT_SUMMARY_PROMPT = """
            用户提供了一个复杂问题：

            ${complex_task}

            问题被拆解成了以下的计划并被执行完毕：

            ${planning_detail}

            计划执行过程中搜索到的所有参考资料如下：

            「参考资料」

            ${reference_detail}

            请根据的上述计划执行的结果和参考资料，进行总结性的回复

            要求：如果回复内容中参考了「参考资料」中的信息，在请务必在正文的段落中引用对应的参考编号，例如[3][5]
            """;
}