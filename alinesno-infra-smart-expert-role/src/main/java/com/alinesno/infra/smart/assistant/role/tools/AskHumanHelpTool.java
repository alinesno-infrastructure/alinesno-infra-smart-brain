package com.alinesno.infra.smart.assistant.role.tools;

import com.alinesno.infra.smart.assistant.annotation.ParamInfo;
import com.alinesno.infra.smart.assistant.annotation.ToolInfo;
import com.alinesno.infra.smart.assistant.plugin.tool.Tool;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 最终结果实现
 */
@Slf4j
@ToolInfo(description = "如果需要人类帮助，或者对事情不清楚的存在疑问的，可以使用这个工具，请使用它。")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class AskHumanHelpTool extends Tool {

    @ParamInfo(description = "需要问人类的问题")
    private String question ;

    public AskHumanHelpTool(String question) {
        this.question = question;
    }

    @Override
    public String execute() {
        log.debug("askHumanHelpTool : {}", question);
        return question ;
    }

    @Override
    public boolean isFinished() {
        return true ;
    }

}
