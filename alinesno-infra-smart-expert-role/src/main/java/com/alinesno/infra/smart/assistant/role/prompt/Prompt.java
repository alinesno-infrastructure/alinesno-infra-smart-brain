package com.alinesno.infra.smart.assistant.role.prompt;

import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.tools.AskHumanHelpTool;
import com.alinesno.infra.smart.assistant.role.tools.RagTool;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

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
                                     String datasetKnowledgeDocument,
                                     int maxLoop ,
                                     boolean hasOutsideKnowledge){

        return String.format(PromptTemplate.corePrompt,
                agent.getPromptContent(),
                datasetKnowledgeDocument,
                thought.toString() ,
                parsePlugins(tools , agent.isAskHumanHelp() ,  hasOutsideKnowledge),
                taskPrompt(goal),
                maxLoop ,
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
    public static List<String> parsePlugins(List<ToolDto> tools , boolean askHumanHelp , boolean hasOutsideKnowledge) {
        List<String> toolList = new ArrayList<>() ;

        // 用户自定义的工具类
        if(!CollectionUtils.isEmpty(tools)){
            for (ToolDto tool : tools) {
                toolList.add(tool.getToolInfo()) ;
            }
        }

        if(askHumanHelp){
            toolList.add(new AskHumanHelpTool().toJson()) ;
        }

        // 使用外部工具知识库
        if(hasOutsideKnowledge){
            toolList.add(new RagTool().toJson()) ;
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

    /**
     * 构建总结的Prompt
     * @return
     */
    public static String buildSummaryPrompt(String goal ,
                                            String datasetKnowledgeDocument,
                                            List<WorkerResponseJson> workerResponseJsons){
       
        String agentThought = formatWorkerThought(workerResponseJsons) ; 
        
        return String.format(PromptTemplate.summaryPrompt , 
                goal ,
                datasetKnowledgeDocument , 
                agentThought) ;
    }

    private static String formatWorkerThought(List<WorkerResponseJson> workerResponseJsons) {

        StringBuilder sb = new StringBuilder();
        if(CollectionUtils.isNotEmpty(workerResponseJsons)){
            for(WorkerResponseJson workerResponseJson : workerResponseJsons){
                String executeToolOutput = workerResponseJson.getExecuteToolOutput() ;
                String thought = workerResponseJson.getThought() ;
                String answer = workerResponseJson.getFinalAnswer() ;

                // 将上面的拼接成一个字符串
                sb.append(String.format(AgentConstants.Slices.THOUGHT ,thought)) ;
                sb.append(String.format(AgentConstants.Slices.EXECUTE_TOOL_RESULT ,executeToolOutput)) ;
                sb.append(String.format(AgentConstants.Slices.ANSWER_RESULT,answer)) ;
                sb.append("----");
            }
        }

        return sb.toString() ;
    }

}
