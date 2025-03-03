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
1.所选工具必须是工具列表中的工具之一，每次只能选择最符合的一个工具，每次只能返回1个json，如果返回两个json结果会容易导致解析异常。
2.你必须用中文回答；
3.您应该只以JSON格式响应
    ###响应格式###
    ```json
    {
        "thought": "<思考为什么选择这个工具的思考>",
        "tools": [{name: "<工具名>","args_list": {"参数名1": "参数值1","参数名2": "参数值2"}}],
        "finalAnswer": "<是否为空是判断你是否已经完成这个任务，如果对原始输入的问题输出最终的答案时，在这里输出结果(不能为空)，当你还没有答案时，为空>"
    }
    ```
    
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
