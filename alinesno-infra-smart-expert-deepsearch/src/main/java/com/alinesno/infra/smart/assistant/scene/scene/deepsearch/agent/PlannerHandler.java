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
import com.alinesno.infra.smart.assistant.role.prompt.PromptHandle;
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
        params.put("tool_info", JSON.toJSONString(PromptHandle.parsePlugins(getTools(), false , false)));
        params.put("max_plannings", maxPlannings);
        params.put("current_time", PromptHandle.getCurrentTime());

        String plannerPrompt = FreemarkerUtil.processTemplate(PlanningPrompts.DEFAULT_PLANNING_MAKE_PROMPT, params);
        historyPrompt.addMessage(new HumanMessage(plannerPrompt));

        CompletableFuture<String> future = new CompletableFuture<>();

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
        stepActionDto.setActionType(StepActionEnums.ANALYSIS.getActionType());
        stepActionDto.setActionName(stepActionDto.getActionName() + " | " + goal);

        llm.chatStream(historyPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {
                AiMessage message = response.getMessage();
                String content = message.getContent();
                String reasoning = message.getReasoningContent();

                // 更新步骤状态（无需同步，单线程回调）
                stepActionDto.setResult(StringUtils.hasLength(content) ? content : "");
                stepActionDto.setThink(StringUtils.hasLength(reasoning) ? reasoning : "");
                stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

                try {
                    // 检查流式响应是否结束
                    if (message.getStatus() == MessageStatus.END) {
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                        // 直接用最终结果完成 Future（无需 AtomicReference）
                        future.complete(message.getFullContent());
                    }

                    // 更新步骤流事件（单线程执行，无需同步）
                    DeepSearchFlow.Plan plan = getDeepSearchFlow().getPlan();
                    plan.addStepAction(stepActionDto);
                    getDeepSearchFlow().setPlan(plan);
                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole(), getTaskInfo());

                } catch (Exception e) {
                    log.error("步骤事件处理失败", e);
                    future.completeExceptionally(e); // 异常时标记 Future 失败
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("LLM 流式调用失败", throwable);
                future.completeExceptionally(throwable); // 失败时完成 Future
            }
        });

        return future;
    }

    /**
     * 异步获取格式化的任务列表（改造后返回 CompletableFuture）
     */
    @SneakyThrows
    public CompletableFuture<List<DeepTaskBean>> getAiChatPlanAsync(
            Llm llm, HistoriesPrompt historyPrompt, MessageTaskInfo taskInfo, String oneChatId, String output) {

        Map<String, Object> params = new HashMap<>();
        params.put("plan_content", output);
        params.put("current_time", PromptHandle.getCurrentTime());

        String plannerPrompt = FreemarkerUtil.processTemplate(PlanningPrompts.DEFAULT_PLANNING_MAKE_PROMPT_FORMATTED, params);
        CompletableFuture<String> formatFuture = new CompletableFuture<>();

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
        stepActionDto.setActionType(StepActionEnums.TOOL.getActionType());

        // 流式调用 LLM，异步获取格式化结果
        llm.chatStream(plannerPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {
                AiMessage message = response.getMessage();
                stepActionDto.setResult(StringUtils.hasLength(message.getContent()) ? message.getContent() : "");
                stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent()) ? message.getReasoningContent() : "");
                stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

                try {
                    if (message.getStatus() == MessageStatus.END) {
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                        formatFuture.complete(message.getFullContent()); // 完成 Future
                    }

                    // 更新步骤事件
                    DeepSearchFlow.Plan plan = getDeepSearchFlow().getPlan();
                    plan.addStepAction(stepActionDto);
                    getDeepSearchFlow().setPlan(plan);
                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole(), getTaskInfo());
                } catch (Exception e) {
                    log.error("格式化任务列表失败", e);
                    formatFuture.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("LLM 格式化任务流调用失败", throwable);
                formatFuture.completeExceptionally(throwable);
            }
        });

        // 解析格式化结果为任务列表（异步链式调用）
        return formatFuture.thenApply(formatOutput -> {
            log.debug("格式化后的规划输出: {}", formatOutput);
            return getTaskList(formatOutput); // 调用现有 getTaskList 方法解析 JSON
        });
    }

}
