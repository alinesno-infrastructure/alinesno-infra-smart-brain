package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.role.prompt.PromptHandle;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepSearchContext;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepTaskBean;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt.PlanningPrompts;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.FreemarkerUtil;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * 规划处理器（无状态，所有依赖通过context显式传入）
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PlannerHandler {

    /**
     * 生成规划任务列表（异步）
     */
    public CompletableFuture<String> getAiChatResultAsync(
            Llm llm,
            HistoriesPrompt historyPrompt,
            MessageTaskInfo taskInfo,
            String oneChatId,
            String goal,
            DeepSearchContext context
    ) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("complex_task", goal);
            params.put("dataset_knowledge_info", context.getDatasetKnowledgeDocument());
            params.put("tool_info", JSON.toJSONString(PromptHandle.parsePlugins(context.getTools(), false, context.isHasOutsideKnowledge() , context.isHasUploadKnowledge())));
            params.put("max_plannings", context.getRole().getAgentTaskPlanCount() == null ? 3 : context.getRole().getAgentTaskPlanCount());
            params.put("current_time", PromptHandle.getCurrentTime());

            String plannerPrompt = FreemarkerUtil.processTemplate(PlanningPrompts.DEFAULT_PLANNING_MAKE_PROMPT, params);
            historyPrompt.addMessage(new HumanMessage(plannerPrompt));

            CompletableFuture<String> future = new CompletableFuture<>();

            DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
            stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
            stepActionDto.setActionType(StepActionEnums.ANALYSIS.getActionType());
            stepActionDto.setActionName(stepActionDto.getActionName() + " | " + goal);
            stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

            // 添加任务记录（通过context访问recordManager）
            context.getRecordManager().addTaskPlanSingleStep(
                    context.getDeepSearchTask().getId(),
                    context.getDeepSearchTask().getId(),
                    goal,
                    context.getDeepSearchFlow().getFlowId(),
                    context.getDeepSearchFlow().getPlan(),
                    stepActionDto ,
                    context.getSessionId()
            );

            // 流式调用LLM
            llm.chatStream(historyPrompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext chatContext, AiMessageResponse response) {
                    var message = response.getMessage();
                    stepActionDto.setResult(message.getContent() != null ? message.getContent() : "");
                    stepActionDto.setThink(message.getReasoningContent() != null ? message.getReasoningContent() : "");

                    try {
                        if (message.getStatus() == MessageStatus.END) {
                            stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                            stepActionDto.setResult(message.getFullContent());
                            stepActionDto.setThink(message.getFullReasoningContent());
                            context.getRecordManager().markTaskPlanSingleStep(stepActionDto, StepActionStatusEnums.DONE.getKey() , context.getSessionId());
                            future.complete(message.getFullContent());
                        }

                        // 更新步骤事件（通过context访问stepEventUtil）
                        var plan = context.getDeepSearchFlow().getPlan();
                        plan.addStepAction(stepActionDto);
                        context.getDeepSearchFlow().setPlan(plan);
                        context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), taskInfo);
                    } catch (Exception e) {
                        log.error("规划生成失败", e);
                        future.completeExceptionally(new CompletionException(e));
                    }
                }

                @Override
                public void onFailure(ChatContext chatContext, Throwable throwable) {
                    log.error("LLM调用失败", throwable);
                    future.completeExceptionally(new CompletionException(throwable));
                }
            });
            return future;
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new CompletionException("规划生成异常", e));
        }
    }

    /**
     * 解析规划结果为任务列表（异步）
     */
    public CompletableFuture<List<DeepTaskBean>> getAiChatPlanAsync(
            Llm llm,
            HistoriesPrompt historyPrompt,
            MessageTaskInfo taskInfo,
            String oneChatId,
            String output,
            DeepSearchContext context
    ) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("plan_content", output);
            params.put("current_time", PromptHandle.getCurrentTime());
            String plannerPrompt = FreemarkerUtil.processTemplate(PlanningPrompts.DEFAULT_PLANNING_MAKE_PROMPT_FORMATTED, params);

            CompletableFuture<String> formatFuture = new CompletableFuture<>();
            DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
            stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
            stepActionDto.setActionType(StepActionEnums.TOOL.getActionType());
            stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

            // 添加任务记录（通过context访问recordManager）
            context.getRecordManager().addTaskPlanSingleStep(
                    context.getDeepSearchTask().getId(),
                    context.getDeepSearchTask().getId(),
                    context.getGoal(),
                    context.getDeepSearchFlow().getFlowId(),
                    context.getDeepSearchFlow().getPlan(),
                    stepActionDto ,
                    context.getSessionId()
            );

            // 流式调用LLM
            llm.chatStream(plannerPrompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext chatContext, AiMessageResponse response) {
                    var message = response.getMessage();
                    stepActionDto.setResult(message.getContent() != null ? message.getContent() : "");
                    stepActionDto.setThink(message.getReasoningContent() != null ? message.getReasoningContent() : "");

                    try {
                        if (message.getStatus() == MessageStatus.END) {
                            stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                            stepActionDto.setResult(message.getFullContent());
                            stepActionDto.setThink(message.getFullReasoningContent());
                            context.getRecordManager().markTaskPlanSingleStep(stepActionDto, StepActionStatusEnums.DONE.getKey() , context.getSessionId());
                            formatFuture.complete(message.getFullContent());
                        }

                        // 更新步骤事件（通过context访问stepEventUtil）
                        var plan = context.getDeepSearchFlow().getPlan();
                        plan.addStepAction(stepActionDto);
                        context.getDeepSearchFlow().setPlan(plan);
                        context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), taskInfo);
                    } catch (Exception e) {
                        log.error("规划格式化失败", e);
                        formatFuture.completeExceptionally(new CompletionException(e));
                    }
                }

                @Override
                public void onFailure(ChatContext chatContext, Throwable throwable) {
                    log.error("LLM格式化调用失败", throwable);
                    formatFuture.completeExceptionally(new CompletionException(throwable));
                }
            });

            // 解析结果为任务列表
            return formatFuture.thenApply(formatOutput -> {
                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(formatOutput);
                CodeContent codeContent = codeContentList.get(0);
                List<DeepTaskBean> tasks = JSON.parseObject(codeContent.getContent(), new com.alibaba.fastjson.TypeReference<List<DeepTaskBean>>() {});
                tasks.forEach(task -> task.setId(IdUtil.getSnowflakeNextIdStr()));
                return tasks;
            });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new CompletionException("规划解析异常", e));
        }
    }
}