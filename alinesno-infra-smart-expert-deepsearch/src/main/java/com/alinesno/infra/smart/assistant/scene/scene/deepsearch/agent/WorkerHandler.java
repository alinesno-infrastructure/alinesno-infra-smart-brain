package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.api.ToolResult;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.prompt.Prompt;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepTaskBean;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt.WorkerPrompt;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.FreemarkerUtil;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 任务执行器，这里使用ReAct模式运行任务，然后获取到FinalAnswer字段
 *
 * @author luoxiaodong
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
public class WorkerHandler extends BaseHandler {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10); // 自定义线程池

    private int maxLoopCount ;
    private String oneChatId;  // 当前聊天步骤ID
    private Llm llm;  // LLM模型
    private List<DeepTaskBean> taskStepMap ;  // 任务规划步骤
    private DeepSearchFlow.Step step ;

    /**
     * 执行任务，并获取到执行结果
     *
     * @param task
     * @return
     */
    public String executeTask(DeepTaskBean task) {

        String answer = "";
        int loopCount = 0;
        boolean isCompleted = false;

        do {
            // 循环执行，确认达到目标，则跳出
            StringBuilder toolOutput = new StringBuilder(); // 工具执行结果
            String workerOutput = executeWorker(task , toolOutput);

            // ----->>>>>>>>>>>>>>>>>>> 解析出需要调用的方法_start ----->>>>>>>>>>>>>>>>>
            List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(workerOutput);
            CodeContent codeContent = codeContentList.get(0);

            WorkerResponseJson reactResponse = JSON.parseObject(codeContent.getContent(), WorkerResponseJson.class);

            // 获取到工具名称，拼接名称起来
            String useToolName = reactResponse.getTools().stream()
                    .map(tool -> tool.getName() + "(" + tool.getType() + ")")
                    .collect(Collectors.joining(","));

            if (reactResponse.getTools() != null && !reactResponse.getTools().isEmpty()) {
                for (WorkerResponseJson.Tool tool : reactResponse.getTools()) {
                    log.debug("正在执行工具名称：{}" , tool.getName());

                    DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
                    stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());

                    stepActionDto.setActionType(StepActionEnums.TOOL.getActionType());
                    stepActionDto.setThink(reactResponse.getThought()) ;
                    stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());
                    step.addAction(stepActionDto);
                    getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId())); // 更新step信息，通过id删除掉原来的step
                    getDeepSearchFlow().getSteps().add(step);
                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());

                    String toolFullName = tool.getName() ;

                    stepActionDto.setActionType(StepActionEnums.TOOL.getActionType());
                    stepActionDto.setActionName(stepActionDto.getActionName() + "|" + useToolName);

                    step.addAction(stepActionDto);

                    // 更新step信息，通过id删除掉原来的step
                    getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
                    getDeepSearchFlow().getSteps().add(step);

                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
                    log.debug("正在执行工具名称：{}" , toolFullName);

                    Map<String, String> argsList = tool.getArgsList();
                    ToolResult toolResult = null;

                    try {

                        if(tool.getType().equals("stdio")){
                            ToolEntity toolEntity = getToolService().getToolScript(toolFullName , getRole().getSelectionToolsData()) ;
                            toolResult = ToolExecutor.executeGroovyScript(toolEntity.getGroovyScript(), argsList , getSecretKey());
                        }else if(tool.getType().equals("mcp")){
                            toolResult = getToolService().executeMcpTool(tool.getId() , argsList , getRole().getOrgId()) ;
                        }else{
                            continue;
                        }

                        Object executeToolOutput = toolResult.getOutput() ;
                        toolOutput.append(String.format("当前执行次数[%s],使用工具[%s] \r\n执行结果:%s" , loopCount , toolFullName , executeToolOutput));

                        // 更新step信息，通过id删除掉原来的step
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                        stepActionDto.setResult(executeToolOutput + "");
                        getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
                        getDeepSearchFlow().getSteps().add(step);

                        getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());

                        if(toolResult.isFinished()){  // 设置工具执行结果即是答案
                            answer = String.valueOf(executeToolOutput) ;
                            isCompleted = true ;   // 结束对话
                        }

                    } catch (Exception e) {
                        log.error("执行工具异常：{}" , e.getMessage());
                        // 更新step信息，通过id删除掉原来的step
                        stepActionDto.setStatus(StepActionStatusEnums.FAIL.getKey());
                        stepActionDto.setResult(e.getMessage());
                        getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
                        getDeepSearchFlow().getSteps().add(step);

                        getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
                    }

                }
            }else if(StringUtils.hasLength(reactResponse.getFinalAnswer())){  // 有了最终的答案
                answer = reactResponse.getFinalAnswer();
                isCompleted = true;
            }
            // ----->>>>>>>>>>>>>>>>>>> 解析出需要调用的方法_end ----->>>>>>>>>>>>>>>>>

            log.debug("answer = {} , isCompleted = {}" , answer , isCompleted);

            if(loopCount >= maxLoopCount){
                isCompleted = true ;
                answer = StringUtils.hasText(answer) ? answer : AgentConstants.ChatText.CHAT_NO_ANSWER ; // 没有找到答案
            }

            loopCount ++ ;
        } while (!isCompleted);

        return answer ;

    }

    /**
     * 执行任务
     *
     * @param task
     * @param toolOutput
     * @return
     */
    @SneakyThrows
    private String executeWorker(DeepTaskBean task, StringBuilder toolOutput) {

        Map<String, Object> params = getStringObjectMap(task , toolOutput);

        String workerPrompt = FreemarkerUtil.processTemplate(WorkerPrompt.DEFAULT_WORKER_PROMPT, params);

        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());

        llm.chatStream(workerPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {

                AiMessage message = response.getMessage();

                stepActionDto.setActionType(StepActionEnums.ANALYSIS.getActionType());
                stepActionDto.setResult(StringUtils.hasLength(message.getContent())?message.getContent():"");
                stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent())?message.getReasoningContent():"");
                stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey()) ;

                try {
                    boolean isEnd = false;
                    if (message.getStatus() == MessageStatus.END) {
                        outputStr.set(message.getFullContent());
                        isEnd = true;
                    }

                    step.addAction(stepActionDto);

                    if (isEnd) {
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey()) ;
                        future.complete(outputStr.get());
                    }

                    // 更新step信息，通过id删除掉原来的step
                    getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
                    getDeepSearchFlow().getSteps().add(step);

                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
                } catch (Exception e) {
                    // 处理发布事件时的异常
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("消息处理失败", throwable);
                future.completeExceptionally(throwable);
            }
        });

        try {
            return future.get(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMessage = "执行任务时线程被中断: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        } catch (ExecutionException e) {
            String errorMessage = "执行任务时出现执行异常: " + e.getCause().getMessage();
            log.error(errorMessage, e.getCause());
            return errorMessage;
        } catch (TimeoutException e) {
            String errorMessage = "执行任务时超时: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    @NotNull
    private Map<String, Object> getStringObjectMap(DeepTaskBean task, StringBuilder toolOutput) {
        String envInfo = toolOutput.toString();
        String planningDetail = getPlanningDetail() ;
        String taskId = task.getId();
        String taskDescription = task.getTaskDesc();

        Map<String, Object> params = new HashMap<>();
        params.put("observation_info", envInfo);
        params.put("dataset_knowledge_info", getDatasetKnowledgeDocument());  // 知识库
        params.put("planning_detail", planningDetail);
        params.put("task_id", taskId);
        params.put("task_description", taskDescription);
        params.put("tool_info", JSON.toJSONString(Prompt.parsePlugins(getTools(), false , false)));
        params.put("current_time", Prompt.getCurrentTime());
        return params;
    }

    private String getPlanningDetail() {

        // 返回所有的任务规划列表
        StringBuilder planningDetail = new StringBuilder();

        for (DeepTaskBean task : taskStepMap) {
            planningDetail.append("任务顺序:").append(task.getOrder()).append("\n")
                    .append("任务ID:").append(task.getId()).append("\n")
                    .append("任务名称:").append(task.getTaskName()).append("\n")
                    .append("任务描述:").append(task.getTaskDesc()).append("\n")
                    .append("\n");
        }

        return planningDetail.toString();
    }
}