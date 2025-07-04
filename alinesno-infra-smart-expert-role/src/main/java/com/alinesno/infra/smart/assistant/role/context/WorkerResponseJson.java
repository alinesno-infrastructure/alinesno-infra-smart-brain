package com.alinesno.infra.smart.assistant.role.context;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data // Lombok注解，自动生成getter/setter等方法
public class WorkerResponseJson {

    @JSONField(name = "thought")
    private String thought; // 对应JSON中的"thought"字段

    private String executeToolOutput =  "" ;  // 工具执行的输出结果

    @JSONField(name = "finalAnswer")
    private String finalAnswer; // 对应JSON中的"thought"字段

    @Data // 内部类也使用Lombok的@Data注解
    public static class Tool {

        @JSONField(name = "id")
        private String id; // 对应JSON中的"id"字段

        @JSONField(name = "type")
        private String type; // 对应的工具类型,mcp或者stdio

        @JSONField(name = "name")
        private String name; // 对应JSON中的"name"字段

        @JSONField(name = "args_list")
        private Map<String, String> argsList; // 对应JSON中的"args_list"字段
    }

    @JSONField(name = "tools")
    private List<Tool> tools; // 对应JSON中的"tools"数组

    public static final String TASK_COMPLETE_TOOL = "TaskCompleteTool";
    public static final String ASK_LEADER_HELP_TOOL = "AskLeaderHelpTool";

    /**
     * 获取名为TaskCompleteTool的工具的参数值answer。
     * 
     * @return 如果找到该工具，则返回其args_list中的answer值；否则返回null。
     */
    public String getTaskCompleteToolAnswer() {
        if (tools == null) {
            return null;
        }

        return tools.stream()
                .filter(tool -> TASK_COMPLETE_TOOL.equals(tool.getName()))
                .map(tool -> tool.getArgsList().get("finalAnswer"))
                .findFirst()
                .orElse(null);
    }

    // 判断是否方法里面包含有 TASK_COMPLETE_TOOL
    public boolean hasTaskCompleteTool() {
        return tools != null && tools.stream().anyMatch(tool -> TASK_COMPLETE_TOOL.equals(tool.getName()));
    }

    // 判断是否包含有
    public boolean hasAskLeaderHelpTool() {
        return tools != null && tools.stream().anyMatch(tool -> ASK_LEADER_HELP_TOOL.equals(tool.getName()));
    }

    // 获取到hasAskLeaderHelpTool的问题
    public String getAskLeaderHelpToolQuestion() {
        if (tools == null) {
            return null;
        }
        return tools.stream()
                .filter(tool -> ASK_LEADER_HELP_TOOL.equals(tool.getName()))
                .map(tool -> tool.getArgsList().get("question"))
                .findFirst()
                .orElse(null);
    }

}