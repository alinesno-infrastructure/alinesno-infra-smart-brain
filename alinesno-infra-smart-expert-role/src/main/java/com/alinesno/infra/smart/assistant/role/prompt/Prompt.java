package com.alinesno.infra.smart.assistant.role.prompt;

import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.context.AgentConstants;
import com.alinesno.infra.smart.assistant.role.tools.AskHumanHelpTool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class Prompt {

    /**
     * 构建Agent的Prompt
     * @return
     */
    public static String buildPrompt(IndustryRoleEntity agent,
                                     List<ToolDto> tools,
                                     StringBuilder thought,
                                     String goal){

        return String.format(PromptTemplate.corePrompt,
                agent.getRoleName(),
                agent.getBackstory() ,
                agent.getResponsibilities() ,
                thought.toString() ,
                parsePlugins(tools , agent.isAskHumanHelp()),
                taskPrompt(goal)
        );

    }

    /**
     * 解析工具
     * @param tools
     * @return
     */
    private static List<String> parsePlugins(List<ToolDto> tools , boolean askHumanHelp) {
        List<String> toolList = new ArrayList<>() ;

        // 用户自定义的工具类
        for (ToolDto tool : tools) {
            toolList.add(tool.getToolInfo()) ;
        }

        if(askHumanHelp){
            toolList.add(new AskHumanHelpTool().toJson()) ;
        }

        return toolList;
    }

    public static String taskPrompt(String goal) {
        return AgentConstants.Slices.TASK.formatted(goal) + "\n" +
               AgentConstants.Slices.EXPECTED_OUTPUT;
    }

    public static String buildHumanHelpPrompt(String prompt, StringBuilder askHumanHelpThought) {
        return String.format(AgentConstants.Slices.MEMORY, askHumanHelpThought.toString()) + prompt;
    }
}
