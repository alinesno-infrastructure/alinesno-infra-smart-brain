package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.ToolResult;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.prompt.PromptHandle;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepSearchContext;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepTaskBean;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt.WorkerPrompt;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.FreemarkerUtil;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 任务执行处理器（无状态，所有依赖通过 DeepSearchContext 显式传入）
 */
@Component
@Slf4j
public class WorkerHandler {

    /**
     * 执行单个任务（同步方法，期望在线程池中调用）
     *
     * @param task        当前任务
     * @param llm         LLM 实例
     * @param taskStepMap 全部任务规划（用于 planning detail）
     * @param context     执行上下文（不可变）
     * @return 返回该任务的文本结果
     */
    public String executeTask(DeepTaskBean task,
                              Llm llm,
                              List<DeepTaskBean> taskStepMap,
                              DeepSearchContext context) {

        StringBuilder toolOutput = new StringBuilder(); // 工具执行结果
        String answer = StringUtils.EMPTY;
        boolean isCompleted = false;
        int loopCount = 0;

        List<WorkerResponseJson> workerResponseJsons = new ArrayList<>();

        // 创建 step 对象并通过 context.deepSearchFlow 管理（与原逻辑一致）
        DeepSearchFlow.Step step = new DeepSearchFlow.Step();
        step.setId(task.getId());
        step.setName(task.getTaskName());
        step.setDescription(task.getTaskDesc());
        // 将 step 写入 flow 的 steps 列表
        List<DeepSearchFlow.Step> steps = context.getDeepSearchFlow().getSteps();
        if (steps == null) {
            steps = new ArrayList<>();
            context.getDeepSearchFlow().setSteps(steps);
        }
        steps.add(step);
        context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());

        do {
            // 循环执行，确认达到目标则跳出
            String workerOutput = executeWorker(task, toolOutput, context.getMaxLoopCount(), llm, taskStepMap, context, step);

            // 解析出需要调用的方法
            List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(workerOutput);
            if (codeContentList.isEmpty()) {
                log.warn("未解析到代码块，workerOutput: {}", workerOutput);
                // 若无代码块，作为文本返回，跳出循环
                answer = workerOutput;
                isCompleted = true;
            } else {
                CodeContent codeContent = codeContentList.get(0);
                WorkerResponseJson reactResponse = JSON.parseObject(codeContent.getContent(), WorkerResponseJson.class);

                // 获取工具名
                String useToolName = reactResponse.getTools() == null ? "" :
                        reactResponse.getTools().stream()
                                .map(t -> t.getName() + "(" + t.getType() + ")")
                                .collect(Collectors.joining(","));

                if (reactResponse.getTools() != null && !reactResponse.getTools().isEmpty()) {
                    // 执行工具调用
                    for (WorkerResponseJson.Tool tool : reactResponse.getTools()) {
                        log.debug("正在执行工具：{}，任务：{}", tool.getName(), task.getTaskName());

                        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
                        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
                        stepActionDto.setActionType(StepActionEnums.TOOL.getActionType());
                        stepActionDto.setThink(reactResponse.getThought());
                        stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());
                        stepActionDto.setActionName(stepActionDto.getActionName() + "|" + useToolName);

                        // 将 action 添加进 step，并发布事件
                        step.addAction(stepActionDto);
                        // 更新 flow steps（替换）
                        context.getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
                        context.getDeepSearchFlow().getSteps().add(step);
                        context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());

                        // 记录步骤信息到 recordManager（异步记录方法原样调用）
                        context.getRecordManager().addTaskWorkerStepActionSingleAsync(
                                context.getDeepSearchTask().getId(),
                                context.getDeepSearchTask().getSceneId(),
                                context.getGoal(),
                                context.getDeepSearchFlow().getFlowId(),
                                stepActionDto,
                                step);

                        Map<String, String> argsList = tool.getArgsList();
                        ToolResult toolResult = null;
                        try {
                            String toolFullName = tool.getName();
                            if ("stdio".equals(tool.getType())) {
                                ToolEntity toolEntity = context.getToolService().getToolScript(toolFullName, context.getRole().getSelectionToolsData());
                                toolResult = ToolExecutor.executeGroovyScript(toolEntity.getGroovyScript(), argsList, context.getSecretKey());
                            } else if ("mcp".equals(tool.getType())) {
                                toolResult = context.getToolService().executeMcpTool(tool.getId(), argsList, context.getRole().getOrgId());
                            } else {
                                log.warn("未知工具类型：{}", tool.getType());
                                continue;
                            }

                            Object executeToolOutput = toolResult.getOutput();
                            toolOutput.append(String.format("\r\n ## 当前执行次数[%s],使用工具[%s] \r\n执行结果:%s", loopCount, toolFullName, executeToolOutput));

                            // 更新 stepAction 状态为 DONE
                            stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                            stepActionDto.setResult(String.valueOf(executeToolOutput));
                            // 更新 flow step
                            context.getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
                            context.getDeepSearchFlow().getSteps().add(step);
                            context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());

                            // 标记任务已完成
                            context.getRecordManager().markTaskWorkerSingleStep(stepActionDto, StepActionStatusEnums.DONE.getKey());

                            if (toolResult.isFinished()) {
                                answer = String.valueOf(executeToolOutput);
                                isCompleted = true;
                            }
                        } catch (Exception e) {
                            log.error("执行工具异常: {}", e.getMessage(), e);
                            stepActionDto.setStatus(StepActionStatusEnums.FAIL.getKey());
                            stepActionDto.setResult(e.getMessage());
                            context.getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
                            context.getDeepSearchFlow().getSteps().add(step);
                            context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());

                            // 记录失败
                            context.getRecordManager().addTaskWorkerStepActionSingleAsync(
                                    context.getDeepSearchTask().getId(),
                                    context.getDeepSearchTask().getSceneId(),
                                    context.getGoal(),
                                    context.getDeepSearchFlow().getFlowId(),
                                    stepActionDto,
                                    step);
                        }
                    }

                    workerResponseJsons.add(reactResponse);
                } else if (StringUtils.isNotEmpty(reactResponse.getFinalAnswer())) {
                    answer = reactResponse.getFinalAnswer();
                    isCompleted = true;
                }
            }

            log.debug("task={} answer={} isCompleted={}", task.getTaskName(), answer, isCompleted);

            // 超过循环次数处理
            if (loopCount >= context.getMaxLoopCount()) {
                isCompleted = true;
                if (StringUtils.isEmpty(answer)) {
                    String summaryPrompt = PromptHandle.buildSummaryPrompt(context.getGoal(),
                            context.getDatasetKnowledgeDocument(),
                            workerResponseJsons);

                    DeepSearchFlow.StepAction summaryAction = new DeepSearchFlow.StepAction();
                    summaryAction.setActionId(IdUtil.getSnowflakeNextIdStr());
                    summaryAction.setActionType(StepActionEnums.SUMMARY_STEP.getActionType());
                    summaryAction.setStatus(StepActionStatusEnums.DOING.getKey());

                    context.getRecordManager().addTaskWorkerStepActionSingleAsync(
                            context.getDeepSearchTask().getId(),
                            context.getDeepSearchTask().getSceneId(),
                            context.getGoal(),
                            context.getDeepSearchFlow().getFlowId(),
                            summaryAction,
                            step);

                    // 通过 summaryMessageTool 获取最终答案（同步调用，保持原逻辑）
                    answer = context.getSummaryMessageTool().getSummaryAnswer(
                            llm,
                            summaryPrompt,
                            context.getTaskInfo(),
                            context.getRole(),
                            context.getDeepSearchFlow(),
                            step,
                            context.getStepEventUtil(),
                            summaryAction
                    );

                    context.getRecordManager().markTaskWorkerSingleStep(summaryAction, StepActionStatusEnums.DONE.getKey());
                }
                answer = StringUtils.isNotEmpty(answer) ? answer : AgentConstants.ChatText.CHAT_NO_ANSWER;
            }

            loopCount++;
        } while (!isCompleted);

        return answer;
    }

    /**
     * 执行一次 worker 的对话（会发起 llm.chatStream）
     *
     * @return 完整的 LLM 输出内容（blocking 等待）
     */
    private String executeWorker(DeepTaskBean task,
                                 StringBuilder toolOutput,
                                 int maxLoopCount,
                                 Llm llm,
                                 List<DeepTaskBean> taskStepMap,
                                 DeepSearchContext context,
                                 DeepSearchFlow.Step step) {

        Map<String, Object> params = getStringObjectMap(task, toolOutput, taskStepMap, context);
        params.put("max_loop_count", maxLoopCount);

        String workerPrompt = FreemarkerUtil.processTemplate(WorkerPrompt.DEFAULT_WORKER_PROMPT, params);

        HistoriesPrompt historyPrompt = new HistoriesPrompt();
        historyPrompt.addMessage(new SystemMessage(WorkerPrompt.DEFAULT_SYSTEM_WORKER_PROMPT));
        historyPrompt.addMessage(new HumanMessage(workerPrompt));

        CompletableFuture<String> future = new CompletableFuture<>();
        final StringBuilder outputAcc = new StringBuilder();

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
        stepActionDto.setActionType(StepActionEnums.ANALYSIS.getActionType());
        stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

        // 记录步骤信息
        context.getRecordManager().addTaskWorkerStepActionSingleAsync(
                context.getDeepSearchTask().getId(),
                context.getDeepSearchTask().getSceneId(),
                context.getGoal(),
                context.getDeepSearchFlow().getFlowId(),
                stepActionDto,
                step);

        llm.chatStream(historyPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext c, AiMessageResponse response) {
                AiMessage message = response.getMessage();
                stepActionDto.setResult(message.getContent() != null ? message.getContent() : "");
                stepActionDto.setThink(message.getReasoningContent() != null ? message.getReasoningContent() : "");

                try {
                    boolean isEnd = false;
                    if (message.getStatus() == MessageStatus.END) {
                        outputAcc.append(message.getFullContent());
                        isEnd = true;
                    } else {
                        outputAcc.append(message.getContent() != null ? message.getContent() : "");
                    }

                    step.addAction(stepActionDto);

                    if (isEnd) {
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                        stepActionDto.setThink(message.getReasoningContent());
                        stepActionDto.setResult(message.getFullContent());
                        context.getRecordManager().markTaskWorkerSingleStep(stepActionDto, StepActionStatusEnums.DONE.getKey());
                        future.complete(outputAcc.toString());
                    }

                    // 更新 flow step 并发布事件
                    context.getDeepSearchFlow().getSteps().removeIf(s -> s.getId().equals(step.getId()));
                    context.getDeepSearchFlow().getSteps().add(step);
                    context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());

                } catch (Exception e) {
                    log.error("worker 流式回调处理失败", e);
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext c, Throwable throwable) {
                log.error("LLM worker 调用失败", throwable);
                future.completeExceptionally(throwable);
            }
        });

        try {
            return future.get(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("executeWorker 被中断", e);
            return "执行被中断: " + e.getMessage();
        } catch (ExecutionException e) {
            log.error("executeWorker 执行异常", e);
            return "执行异常: " + e.getCause().getMessage();
        } catch (TimeoutException e) {
            log.error("executeWorker 超时", e);
            return "执行超时: " + e.getMessage();
        }
    }

    /**
     * 构造 prompt 参数 map
     */
    private Map<String, Object> getStringObjectMap(DeepTaskBean task, StringBuilder toolOutput, List<DeepTaskBean> taskStepMap, DeepSearchContext context) {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("observation_info", toolOutput.toString());
        params.put("dataset_knowledge_info", context.getDatasetKnowledgeDocument());
        params.put("planning_detail", getPlanningDetail(taskStepMap));
        params.put("task_id", task.getId());
        params.put("task_description", task.getTaskDesc());
        params.put("tool_info", JSON.toJSONString(PromptHandle.parsePlugins(context.getTools(), false, false)));
        params.put("current_time", PromptHandle.getCurrentTime());
        return params;
    }

    /**
     * 拼接所有 planning detail
     */
    private String getPlanningDetail(List<DeepTaskBean> taskStepMap) {
        StringBuilder planningDetail = new StringBuilder();
        for (DeepTaskBean t : taskStepMap) {
            planningDetail.append("任务顺序:").append(t.getOrder()).append("\n")
                    .append("任务ID:").append(t.getId()).append("\n")
                    .append("任务名称:").append(t.getTaskName()).append("\n")
                    .append("任务描述:").append(t.getTaskDesc()).append("\n\n");
        }
        return planningDetail.toString();
    }
}