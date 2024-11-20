package com.alinesno.infra.smart.assistant.role.tools;

import com.alinesno.infra.smart.assistant.annotation.ParamInfo;
import com.alinesno.infra.smart.assistant.annotation.ToolInfo;
import com.alinesno.infra.smart.assistant.plugin.tool.Tool;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 最终结果实现
 */
@ToolInfo(description = "任务完成工具，用于结束任务，输入任务输出。")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class TaskCompleteTool extends Tool {

    @ParamInfo(description = "任务完成之后，内容通过这个字段输出，其它地方内容不做为最后结果")
    private String finalAnswer ;

    public TaskCompleteTool(String finalAnswer) {
        this.finalAnswer = finalAnswer;
    }

    @Override
    public String execute() {
        return "";
    }
}
