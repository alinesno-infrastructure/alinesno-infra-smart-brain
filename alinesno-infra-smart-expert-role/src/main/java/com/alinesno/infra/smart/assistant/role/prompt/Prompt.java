package com.alinesno.infra.smart.assistant.role.prompt;

import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.assistant.role.tools.AskHumanHelpTool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                                     String goal ,
                                     String datasetKnowledgeDocument){

        return String.format(PromptTemplate.corePrompt,
                agent.getPromptContent(),
                datasetKnowledgeDocument,
                thought.toString() ,
                parsePlugins(tools , agent.isAskHumanHelp()),
                taskPrompt(goal),
                getCurrentTime()

        );

    }

    /**
     * 获取到当前时间
     */
    public static String getCurrentTime() {
        // 获取当前日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 将当前日期时间按照指定格式进行格式化
        return currentDateTime.format(formatter);
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
