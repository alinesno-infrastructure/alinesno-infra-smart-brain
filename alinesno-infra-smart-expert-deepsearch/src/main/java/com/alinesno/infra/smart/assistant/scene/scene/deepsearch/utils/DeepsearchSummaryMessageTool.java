package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class DeepsearchSummaryMessageTool {

    // 默认超时时间，可改为从配置注入
    private final Duration defaultTimeout = Duration.ofSeconds(120);

    // 一个可重用的调度线程池用于超时管理
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            Math.max(1, Runtime.getRuntime().availableProcessors() / 2),
            runnable -> {
                Thread t = new Thread(runnable, "SummaryMessageTool-Scheduler");
                t.setDaemon(true);
                return t;
            }
    );

    @PreDestroy
    public void shutdown() {
        try {
            scheduler.shutdownNow();
        } catch (Exception e) {
            log.warn("Error shutting down scheduler", e);
        }
    }

    @SneakyThrows
    public String getSummaryAnswer(Llm llm,
                                   String summaryPrompt,
                                   MessageTaskInfo taskInfo,
                                   IndustryRoleEntity role,
                                   DeepSearchFlow deepSearchFlow,
                                   DeepSearchFlow.Step step,
                                   StepEventUtil stepEventUtil,
                                   DeepSearchFlow.StepAction stepActionDto) {

        CompletableFuture<String> future =
                getSingleAiChatResultAsync(llm,
                        summaryPrompt,
                        taskInfo,
                        role,
                        deepSearchFlow,
                        step,
                        stepEventUtil,
                        stepActionDto,
                        defaultTimeout);
        return future.get(defaultTimeout.toMillis(), TimeUnit.MILLISECONDS); // 为调用方也添加超时保护
    }

    /**
     * 核心异步方法：适配有/无 taskInfo 与 publisher 的两种场景
     * 返回 CompletableFuture，使用者可选择同步阻塞或异步处理
     */
    public CompletableFuture<String> getSingleAiChatResultAsync(Llm llm,
                                                                String prompt,
                                                                MessageTaskInfo taskInfo,
                                                                IndustryRoleEntity role,
                                                                DeepSearchFlow deepSearchFlow,
                                                                DeepSearchFlow.Step step ,
                                                                StepEventUtil stepEventUtil,
                                                                DeepSearchFlow.StepAction stepActionDto,
                                                                Duration timeout) {

        Objects.requireNonNull(llm, "llm cannot be null");
        Objects.requireNonNull(prompt, "prompt cannot be null");

        final CompletableFuture<String> future = new CompletableFuture<>();
        final AtomicBoolean completed = new AtomicBoolean(false);
        final AtomicReference<String> outputRef = new AtomicReference<>("");

        // 超时任务：如果到时间仍未完成，则尝试完成异常
        final ScheduledFuture<?> timeoutHandle = scheduler.schedule(() -> {
            if (completed.compareAndSet(false, true)) {
                String msg = "LLM stream timeout after " + (timeout != null ? timeout.toMillis() : defaultTimeout.toMillis()) + " ms";
                future.completeExceptionally(new TimeoutException(msg));
            }
        }, timeout != null ? timeout.toMillis() : defaultTimeout.toMillis(), TimeUnit.MILLISECONDS);

        try {

            llm.chatStream(prompt, (context, response) -> {
                // 快速检查：如果已经完成（超时或提前完成），忽略后续回调，避免并发竞争
                if (completed.get()) {
                    return;
                }

                try {
                    AiMessage message = response.getMessage();
                    if (message == null) {
                        return;
                    }

                    stepActionDto.setResult(StringUtils.isNotEmpty(message.getContent())?message.getContent(): StringUtils.EMPTY);
                    stepActionDto.setThink(StringUtils.isNotEmpty(message.getReasoningContent())?message.getReasoningContent(): StringUtils.EMPTY);

                    try {
                        // 检查流式响应是否结束
                        if (message.getStatus() == MessageStatus.END) {
                            stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey()) ;

                            stepActionDto.setThink(message.getFullReasoningContent());
                            stepActionDto.setResult(message.getFullContent());

                            // 直接用最终结果完成 Future（无需 AtomicReference）
                            future.complete(message.getFullContent());
                        }

                        step.addAction(stepActionDto);

                        // 更新step信息，通过id删除掉原来的step
                        deepSearchFlow.getSteps().removeIf(s -> s.getId().equals(step.getId()));
                        deepSearchFlow.getSteps().add(step);

                        stepEventUtil.eventStepMessage(deepSearchFlow, role , taskInfo);

                    } catch (Exception e) {
                        log.error("步骤事件处理失败", e);
                        future.completeExceptionally(e); // 异常时标记 Future 失败
                    }

                } catch (Exception e) {
                    log.error("Error processing stream callback", e);
                    if (completed.compareAndSet(false, true)) {
                        timeoutHandle.cancel(false);
                        future.completeExceptionally(e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("Error starting chatStream", e);
            if (completed.compareAndSet(false, true)) {
                timeoutHandle.cancel(false);
                future.completeExceptionally(e);
            }
        }

        // 当外部取消 future 时，保证内部完成标志被设置，避免后续回调继续处理
        future.whenComplete((res, err) -> {
            if (future.isCancelled()) {
                completed.set(true);
            }
            // 取消超时任务，若尚未取消
            timeoutHandle.cancel(false);
        });

        return future;
    }
}