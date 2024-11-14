package com.alinesno.infra.smart.assistant.screen.agent.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 工作器响应JSON对象
 * 该类用于表示从工作器获取到的响应数据结构
 */
@Data
public class WorkerResponseJson {

    public static final String Ask_Human_Help_Tool = "AskHumanHelpTool" ;
    public static final String Task_Complete_Tool = "TaskCompleteTool" ;

    @JSONField(name = "thought")
    private String thought;

    @JSONField(name = "tool_names")
    private List<String> toolNames;

    @JSONField(name = "args_list")
    private Map<String, Map<String, Object>> argsList;

    /**
     * 获取指定工具的提问内容
     *
     * @param toolName 工具名称
     * @return 返回提问内容，如果不存在则返回空字符串
     */
    public String getQuestion(String toolName) {
        if (argsList != null && argsList.containsKey(toolName)) {
            Map<String, Object> toolArgs = argsList.get(toolName);
            if (toolArgs.containsKey("question")) {
                return (String) toolArgs.get("question");
            }
        }
        return "";
    }

    /**
     * 获取指定工具的提问内容
     *
     * @return 返回提问内容，如果不存在则返回空字符串
     */
    public String getQuestion() {
        return getQuestion(Ask_Human_Help_Tool);
    }

    /**
     * 获取指定工具的答案内容
     *
     * @param toolName 工具名称
     * @return 返回答案内容，如果不存在则返回空字符串
     */
    public String getAnswer(String toolName) {
        if (argsList != null && argsList.containsKey(toolName)) {
            Map<String, Object> toolArgs = argsList.get(toolName);
            if (toolArgs.containsKey("answer")) {
                return (String) toolArgs.get("answer");
            }
        }
        return "";
    }

    /**
     * 获取指定工具的答案内容
     *
     * @return 返回答案内容，如果不存在则返回空字符串
     */
    public String getAnswer() {
        return getAnswer(Task_Complete_Tool);
    }

    /**
     * 获取指定工具的所有参数
     *
     * @param toolName 工具名称
     * @return 返回工具的所有参数，如果工具不存在则返回null
     */
    public Map<String, Object> getToolParams(String toolName) {
        if (argsList != null && argsList.containsKey(toolName)) {
            return argsList.get(toolName);
        }
        return null;
    }

}
