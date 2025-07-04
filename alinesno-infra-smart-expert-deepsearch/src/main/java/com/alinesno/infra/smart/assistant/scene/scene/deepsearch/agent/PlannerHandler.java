package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.role.prompt.Prompt;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepTaskBean;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt.PlanningPrompts;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.FreemarkerUtil;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 处理任务工具类
 * @author luoxiaodong
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
public class PlannerHandler extends BaseHandler {

    /**
     * 获取任务列表
     * @param plannerOutput
     * @return
     */
    public List<DeepTaskBean> getTaskList(String plannerOutput) {

        List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(plannerOutput);
        CodeContent codeContent = codeContentList.get(0);

        List<DeepTaskBean> tasks = JSON.parseObject(codeContent.getContent(), new TypeReference<>() {});
        for(DeepTaskBean task : tasks){
            task.setId(IdUtil.getSnowflakeNextIdStr());
        }

        return tasks ;
    }

    /**
     * 获取AI结果
     *
     * @param llm
     * @param historyPrompt
     * @param taskInfo
     * @param oneChatId
     * @param goal
     * @return
     */
    @SneakyThrows
    protected CompletableFuture<String> getAiChatResultAsync(Llm llm, HistoriesPrompt historyPrompt  , MessageTaskInfo taskInfo , String oneChatId, String goal) {

        // 先获取到规划任务内容
        int maxPlannings = getRole().getAgentTaskPlanCount() == null ? 5 : getRole().getAgentTaskPlanCount();

        Map<String, Object> params = new HashMap<>();

        params.put("complex_task", goal);
        params.put("dataset_knowledge_info", getDatasetKnowledgeDocument());
        params.put("tool_info", JSON.toJSONString(Prompt.parsePlugins(getTools(), false , false)));
        params.put("max_plannings", maxPlannings);
        params.put("current_time", Prompt.getCurrentTime());

        String plannerPrompt = FreemarkerUtil.processTemplate(PlanningPrompts.DEFAULT_PLANNING_MAKE_PROMPT, params);
        historyPrompt.addMessage(new HumanMessage(plannerPrompt));

        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        // 创建一个 final 局部变量来持有 taskInfo 的引用
        final MessageTaskInfo localTaskInfo = taskInfo;
        long startTime = System.currentTimeMillis();

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());

        llm.chatStream(historyPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {

                AiMessage message = response.getMessage();

                stepActionDto.setActionType(StepActionEnums.ANALYSIS.getActionType());
                stepActionDto.setActionName(stepActionDto.getActionName() + " | " + goal);
                stepActionDto.setResult(StringUtils.hasLength(message.getContent())?message.getContent():"");
                stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent())?message.getReasoningContent():"");
                stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

                try {
                    boolean isEnd = false;
                    synchronized (localTaskInfo) {
                        if (message.getStatus() == MessageStatus.END) {
                            outputStr.set(message.getFullContent());
                            isEnd = true;
                        }
                    }

                    if (isEnd) {
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                        future.complete(outputStr.get());
                    }

                    DeepSearchFlow.Plan plan = getDeepSearchFlow().getPlan() ;
                    plan.addStepAction(stepActionDto);
                    getDeepSearchFlow().setPlan(plan);

                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
                } catch (Exception e) {
                    // 处理发布事件时的异常
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("消息处理失败" , throwable);
                future.completeExceptionally(throwable);
            }

        }) ;

        return future;
    }

    /**
     * 获取AI计划并且已经格式化的
     * @param llm
     * @param historyPrompt
     * @param taskInfo
     * @param oneChatId
     * @param output
     * @return
     */
    @SneakyThrows
    public List<DeepTaskBean> getAiChatPlanAsync(Llm llm, HistoriesPrompt historyPrompt, MessageTaskInfo taskInfo, String oneChatId, String output) {

        Map<String, Object> params = new HashMap<>();
        params.put("plan_content", output);
        params.put("current_time", Prompt.getCurrentTime());

        String plannerPrompt = FreemarkerUtil.processTemplate(PlanningPrompts.DEFAULT_PLANNING_MAKE_PROMPT_FORMATTED, params);
        // historyPrompt.addMessage(new HumanMessage(plannerPrompt));

        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        // 创建一个 final 局部变量来持有 taskInfo 的引用
        final MessageTaskInfo localTaskInfo = taskInfo;

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());

        llm.chatStream(plannerPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {

                AiMessage message = response.getMessage();

                stepActionDto.setActionType(StepActionEnums.TOOL.getActionType());
                stepActionDto.setResult(StringUtils.hasLength(message.getContent())?message.getContent():"");
                stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent())?message.getReasoningContent():"");
                stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

                try {
                    boolean isEnd = false;
                    synchronized (localTaskInfo) {
                        if (message.getStatus() == MessageStatus.END) {
                            outputStr.set(message.getFullContent());
                            isEnd = true;
                        }
                    }

                    if (isEnd) {
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                        future.complete(outputStr.get());
                    }

                    DeepSearchFlow.Plan plan = getDeepSearchFlow().getPlan() ;
                    plan.addStepAction(stepActionDto);
                    getDeepSearchFlow().setPlan(plan);

                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
                } catch (Exception e) {
                    // 处理发布事件时的异常
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("消息处理失败" , throwable);
                future.completeExceptionally(throwable);
            }

        }) ;

        String formatOutput = future.get() ;

        return getTaskList(formatOutput);
    }
}
