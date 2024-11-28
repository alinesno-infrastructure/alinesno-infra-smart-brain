package com.alinesno.infra.smart.assistant.role;

import cn.hutool.core.util.IdUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.llm.QianWenNewApiLLM;
import com.alinesno.infra.smart.assistant.role.prompt.Prompt;
import com.alinesno.infra.smart.assistant.role.tools.AskHumanHelpTool;
import com.alinesno.infra.smart.assistant.service.IToolService;
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

    @Value("${alinesno.infra.smart.assistant.maxLoop:10}")
    private int maxLoop ;

    @Autowired
    private IToolService toolService ;

    @Autowired
    private QianWenNewApiLLM qianWenNewApiLLM ;

    @Override
    protected String handleRole(IndustryRoleEntity role,
                                MessageEntity workflowExecution,
                                MessageTaskInfo taskInfo) {

        String goal = clearMessage(taskInfo.getText()) ; // 目标
        boolean askHumanHelp = role.isAskHumanHelp() ;

        // 工具类
        List<ToolDto> tools = toolService.getByRole(role.getId()) ;

        boolean isCompleted = false ;  // 是否已经完成
        int loop = 0 ;

        String answer = null; // 回答
        StringBuilder thought = new StringBuilder(); // 思考

        do {
            loop++;

            streamMessagePublisher.doStuffAndPublishAnEvent("开始第"+loop+"次思考 ....",
                    role,
                    taskInfo,
                    IdUtil.getSnowflakeNextId());

            String prompt = Prompt.buildPrompt(role , tools , thought , goal) ;

            List<Message> messages = new ArrayList<>();
            messages.add(qianWenNewApiLLM.createMessage(Role.USER, prompt));
            if(askHumanHelp){
                askHumanHelpRecycle(messages) ;
            }

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {

                StringBuilder outputStr = new StringBuilder();
                Flowable<GenerationResult> result = qianWenNewApiLLM.streamCall(messages);
                long tmpMsgId = IdUtil.getSnowflakeNextId() ;

                StringBuilder preMsg = new StringBuilder() ;

                result.blockingForEach(message -> {

                    String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
                    String finishReason = message.getOutput().getChoices().get(0).getFinishReason() ;

                    streamMessagePublisher.doStuffAndPublishAnEvent(msg.substring(preMsg.toString().length()),
                            role,
                            taskInfo,
                            tmpMsgId);

                    preMsg.setLength(0);
                    preMsg.append(msg) ;

                    if(finishReason != null && finishReason.equals("stop")){
                        outputStr.append(msg);
                    }
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

                if(StringUtils.hasLength(reactResponse.getFinalAnswer())){  // 有了最终的答案
                    answer = reactResponse.getFinalAnswer();
                    isCompleted = true;
                }else if (reactResponse.getTools() != null && !reactResponse.getTools().isEmpty()) {
                    String observation = "" ;
                    for(WorkerResponseJson.Tool tool : reactResponse.getTools()){
                        String toolFullName = tool.getName() ;

                        // 如果是咨询人类的
                        if(toolFullName.equals(AskHumanHelpTool.class.getSimpleName())){
                           String question = tool.getArgsList().get("question")+"" ;

                            streamMessagePublisher.doStuffAndPublishAnEvent(question ,
                                    role,
                                    taskInfo,
                                    IdUtil.getSnowflakeNextId());

                            isCompleted = true ;   // 结束对话，等待人类回复
                            answer = "本轮结束" ;
                            continue;
                        }

                        log.debug("正在执行工具名称：{}" , toolFullName);
                        streamMessagePublisher.doStuffAndPublishAnEvent("正在执行工具:" + toolFullName +"，请稍等..." ,
                                role,
                                taskInfo,
                                IdUtil.getSnowflakeNextId());

                        ToolEntity toolEntity = toolService.getToolScript(toolFullName , role.getId()) ;

                        Map<String, Object> argsList = tool.getArgsList();

                        try {
                            Object executeToolOutput = ToolExecutor.executeGroovyScript(toolEntity.getGroovyScript(), argsList);
                            if(executeToolOutput != null && StringUtils.hasLength(executeToolOutput+"")){

                                String toolAndObservable = String.format("%s\r\n" , executeToolOutput) ;
                                thought.append(toolAndObservable);

                                streamMessagePublisher.doStuffAndPublishAnEvent("工具执行结果:" + executeToolOutput,
                                        role,
                                        taskInfo,
                                        IdUtil.getSnowflakeNextId());

                            }
                            log.debug("工具执行结果：{}", observation);
                            System.out.println();
                        } catch (Exception e) {
                            log.error("工具执行失败:{}", e.getMessage());
                            streamMessagePublisher.doStuffAndPublishAnEvent("工具执行失败:" + e.getMessage(),
                                    role,
                                    taskInfo,
                                    IdUtil.getSnowflakeNextId());
                        }
                    }

                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                streamMessagePublisher.doStuffAndPublishAnEvent("调用失败:" + e.getMessage(),
                        role,
                        taskInfo,
                        IdUtil.getSnowflakeNextId());
            }

            if(loop >= maxLoop){
                isCompleted = true ;
            }
        } while (!isCompleted);

        return StringUtils.hasLength(answer)? answer : "我尝试找了很多次，但是未找到答案";
    }

    /**
     * TODO 处理咨询的历史问题
     * @param messages
     */
    private void askHumanHelpRecycle(List<Message> messages) {
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
