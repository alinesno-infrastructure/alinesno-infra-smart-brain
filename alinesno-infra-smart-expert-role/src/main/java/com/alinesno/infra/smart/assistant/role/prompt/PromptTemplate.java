package com.alinesno.infra.smart.assistant.role.prompt;

public interface PromptTemplate {

    String corePrompt = """
%s

请从现有知识库中获取到答案，尽量准确的回答目标问题，如果现有知识不能满足，选择使用合适的工具解决。
###知识库###
%s
%s
###工具列表###
%s
###约束条件####
1.所选工具必须是工具列表中的工具之一，每次尽量选择最符合的一个工具,如果有必要，可以使用多个工具，每次只能返回1个json，如果返回两个json结果会导致解析异常。
2.你不会断的使用工具来解决问题，每次工具的结果会是下一次执行的输入，多次循环（最多不能超过9次循环）调用工具来执行，直接得到答案
3.你必须用中文回答；
4.您应该只以JSON格式响应
    ###响应格式###
    ```json
        {
            "thought": "<输出为什么在这个场景下选择这个工具的思考>",
            "tools": [
                {
                    "name": "<工具名>",
                    "args_list": {
                        "参数名 1": "参数值 1",
                        "参数名 2": "参数值 2"
                    }
                }
            ],
            "finalAnswer": "<是否为空是判断你是否已经完成这个问题，如果对原始输入的问题输出最终的答案时，在这里输出结果，或者你有不清楚的，可以向提问者进行咨询，在这里输出结果，当你还没有完整答案时这里设置为空>"
        }
    ```
5.如果finalAnswer有值，则不会再执行工具，即使工具有参数。
6.在进行响应时，若涉及到特殊字符的使用，需按照相关的转义规则进行处理，以确保数据的正确解析和使用，避免因字符问题导致的错误或异常情况，比如`的转义字符是\\`

###问题###
%s

###其它####
当前时间:%s
    """;

    String corePromptForEn = """
You are: %s, Background: %s, Expertise: %s, please obtain the answer from your own knowledge or the existing knowledge base as accurately as possible. If the current knowledge cannot meet the requirements, choose to use appropriate tools to solve the problem.
### Knowledge Base ###
%s
### Tool List ###
%s
### Constraints ###
1. The selected tool must be one of the tools in the tool list, and only one most suitable tool can be chosen each time.
2. You must respond in Chinese;
3. Your response should be in JSON format only.
    ### Response Format ###
    ```json
    {
        "thought": "<Reasoning for choosing this tool>",
        "tools": [
            {
                name: "<Tool Name>",
                "args_list": {
                  "param1": "value1",
                  "param2": "value2"
                }
            }
        ]
    }
    ```
    ... (This JSON can repeat zero or more times until a final answer is reached)
    ```json
    {
        "thought": "I now know the final answer",
        "tools": [],
        "finalAnswer": "<Output the final answer to the original input question>"
    }
    ```

### Question ###
%s
    """;

}
