package com.alinesno.infra.smart.assistant.role.prompt;

import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.context.AgentConstants;
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
                                     String roleName,
                                     String goal,
                                     String backstory){

//        return String.format(PromptTemplate.corePrompt,
//                agent.getRole(),
//                agent.getBackstory() ,
//                goal ,
//                agent.getKnowledge(),
//                parsePluginDtos(agent.getPluginDtos())
//        );

        return null ;
    }

    /**
     * 解析工具
     * @param tools
     * @return
     */
    private static List<String> parsePlugins(List<ToolDto> tools) {
        List<String> toolList = new ArrayList<>() ;

//        // 完成任务时
//        PluginDto taskCompletePluginDto = new TaskCompletePluginDto() ;
//        PluginDto askLeaderHelpPluginDto = new AskLeaderHelpPluginDto() ;
//
//        // 用户自定义的工具类
//        for (PluginDto tool : tools) {
//            toolList.add(tool.toJson()) ;
//            toolMap.put(tool.toPluginDtoName() , tool.getFullName()) ;
//        }
//
//        toolList.add(taskCompletePluginDto.toJson())  ;
//        toolList.add(askLeaderHelpPluginDto.toJson()) ;
//
//        toolMap.put(taskCompletePluginDto.toPluginDtoName() , taskCompletePluginDto.getFullName()) ;
//        toolMap.put(askLeaderHelpPluginDto.toPluginDtoName() , askLeaderHelpPluginDto.getFullName()) ;
//
//        for (String toolName : toolMap.keySet()) {
//            log.debug("工具名称：{} , 工具全名：{}" , toolName , toolMap.get(toolName));
//        }

        return toolList;
    }

    public static String taskPrompt(String goal) {
        return AgentConstants.Slices.TASK.formatted(goal) + "\n" +
               AgentConstants.Slices.EXPECTED_OUTPUT;
    }
}
