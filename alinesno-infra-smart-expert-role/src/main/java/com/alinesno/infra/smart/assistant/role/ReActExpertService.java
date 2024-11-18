package com.alinesno.infra.smart.assistant.role;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.llm.QianWenNewApiLLM;
import com.alinesno.infra.smart.assistant.role.prompt.Prompt;
import com.alinesno.infra.smart.assistant.role.tools.Tool;
import com.alinesno.infra.smart.assistant.role.tools.ToolExecutor;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Agent推理模式
 */
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_REACT)
public class ReActExpertService extends ExpertService {

    @Value("${alinesno.infra.smart.assistant.maxLoop:3}")
    private int maxLoop ;

    @Autowired
    private QianWenNewApiLLM qianWenNewApiLLM ;

    @Override
    protected String handleRole(IndustryRoleEntity role,
                                MessageEntity workflowExecution,
                                MessageTaskInfo taskInfo) {

        String roleName = role.getRoleName() ; // 角色名称
        String goal = clearMessage(taskInfo.getText()) ; // 目标
        String backstory = role.getBackstory() ; // 背景

        // 工具类
        List<Tool> tools = new ArrayList<>() ;

        List<Message> messages = new ArrayList<>();
        messages.add(qianWenNewApiLLM.createMessage(Role.SYSTEM, Prompt.buildPrompt(role , roleName , goal, backstory)));
        messages.add(qianWenNewApiLLM.createMessage(Role.USER, Prompt.taskPrompt(goal)));

        boolean isCompleted = false ;  // 是否已经完成
        int loop = 0 ;

        String answer = null; // 回答
        StringBuilder thought = new StringBuilder(); // 思考

        do {
            loop++;

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {

                StringBuilder outputStr = new StringBuilder();

                Flowable<GenerationResult> result = qianWenNewApiLLM.streamCall(messages);
                result.blockingForEach(message -> {

                    String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
                    String finishReason = message.getOutput().getChoices().get(0).getFinishReason() ;

                    log.debug("Received message: {} , finishReason: {}", msg , finishReason);
                    outputStr.append(msg);
                });

                // 生成任务结果
                return outputStr.toString() ;
            });

            // 等待异步任务完成并获取结果
            try {
                String output = future.get();
                log.debug("output = {}" , output);

                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(output);
                CodeContent codeContent = codeContentList.get(0);

                WorkerResponseJson reactResponse = JSON.parseObject(codeContent.getContent(), WorkerResponseJson.class);
                log.debug("reactResponse = {}" , reactResponse);

                // 如果说toolNames不为空，并且有对应的工具，则直接调用
                if (reactResponse.getTools() != null && !reactResponse.getTools().isEmpty()) {

                    if (reactResponse.hasTaskCompleteTool()) {  // 完成的智能体
                        answer = reactResponse.getTaskCompleteToolAnswer();
                        isCompleted = true;
                    } else {
                        String observation = "" ;
                        for(WorkerResponseJson.Tool tool : reactResponse.getTools()){
                            String toolFullName = tool.getName() ;

                            log.debug("正在执行工具名称：{}" , toolFullName);

                            Map<String, Object> argsList = tool.getArgsList();

                            try {
                                Object executeToolOutput = ToolExecutor.executeGroovyScript(toolFullName, argsList);
                                if(executeToolOutput != null && StringUtils.hasLength(executeToolOutput+"")){
                                    thought.append("\n");
                                    observation += (thought.append(executeToolOutput)) ;
                                }
                                log.debug("工具执行结果：{}", observation);
                                System.out.println();
                            } catch (Exception e) {
                                log.error("工具执行失败:{}", e.getMessage());
                            }
                        }

                        messages.add(qianWenNewApiLLM.createMessage(Role.USER, observation));
                    }
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            if(loop >= maxLoop){
                isCompleted = true ;
            }
        } while (!isCompleted);

        return answer;
    }

    @Override
    protected String handleModifyCall(IndustryRoleEntity role,
                                      MessageEntity workflowExecution,
                                      List<CodeContent> codeContentList,
                                      MessageTaskInfo taskInfo) {

        return null;
    }

    @Override
    protected String handleFunctionCall(IndustryRoleEntity role,
                                        MessageEntity workflowExecution,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {

        return null;
    }

}
