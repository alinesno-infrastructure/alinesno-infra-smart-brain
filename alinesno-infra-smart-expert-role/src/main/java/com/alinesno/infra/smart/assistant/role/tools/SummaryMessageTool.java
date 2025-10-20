package com.alinesno.infra.smart.assistant.role.tools;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class SummaryMessageTool {

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
                                   String summaryChatId) {
        return getSummaryAnswer(llm, summaryPrompt, summaryChatId, defaultTimeout);
    }

    @SneakyThrows
    public String getSummaryAnswer(Llm llm,
                                   String summaryPrompt,
                                   String summaryChatId,
                                   Duration timeout) {
        CompletableFuture<String> future = getSingleAiChatResultAsync(llm, summaryPrompt, null, summaryChatId, null, null, timeout);
        return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS); // 为调用方也添加超时保护
    }

    @SneakyThrows
    public String getSummaryAnswer(Llm llm,
                                   String summaryPrompt,
                                   String summaryChatId,
                                   MessageTaskInfo taskInfo,
                                   IndustryRoleEntity role,
                                   StreamMessagePublisher streamMessagePublisher) {
        return getSummaryAnswer(llm, summaryPrompt, summaryChatId, taskInfo, role, streamMessagePublisher, defaultTimeout);
    }

    @SneakyThrows
    public String getSummaryAnswer(Llm llm,
                                   String summaryPrompt,
                                   String summaryChatId,
                                   MessageTaskInfo taskInfo,
                                   IndustryRoleEntity role,
                                   StreamMessagePublisher streamMessagePublisher,
                                   Duration timeout) {
        CompletableFuture<String> future = getSingleAiChatResultAsync(llm, summaryPrompt, taskInfo, summaryChatId, role, streamMessagePublisher, timeout);
        return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 核心异步方法：适配有/无 taskInfo 与 publisher 的两种场景
     * 返回 CompletableFuture，使用者可选择同步阻塞或异步处理
     */
    public CompletableFuture<String> getSingleAiChatResultAsync(Llm llm,
                                                                String prompt,
                                                                MessageTaskInfo taskInfo,
                                                                String summaryChatId,
                                                                IndustryRoleEntity role,
                                                                StreamMessagePublisher streamMessagePublisher,
                                                                Duration timeout) {

        Objects.requireNonNull(llm, "llm cannot be null");
        Objects.requireNonNull(prompt, "prompt cannot be null");
        Objects.requireNonNull(summaryChatId, "summaryChatId cannot be null");

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

                    FlowStepStatusDto stepDto = new FlowStepStatusDto();
                    stepDto.setMessage("任务进行中...");
                    stepDto.setStepId(summaryChatId);
                    stepDto.setStatus(AgentConstants.STEP_PROCESS);

                    if (StringUtils.hasLength(message.getContent())) {
                        stepDto.setFlowChatText(message.getContent());
                    }

                    if (StringUtils.hasLength(message.getReasoningContent())) {
                        stepDto.setFlowReasoningText(message.getReasoningContent());
                    }

                    stepDto.setPrint(true);

                    // 仅在需要更新 taskInfo 时短时间同步
                    if (taskInfo != null) {
                        synchronized (taskInfo) {
                            taskInfo.setFlowStep(stepDto);
                        }
                    }

                    // 当流结束时，记录完整内容并尝试完成 future（一次性）
                    if (message.getStatus() == MessageStatus.END) {
                        outputRef.set(message.getFullContent());
                        stepDto.setStatus(AgentConstants.STEP_FINISH);

                        // 更新 taskInfo 的状态 再发布事件
                        if (taskInfo != null) {
                            synchronized (taskInfo) {
                                taskInfo.setFlowStep(stepDto);
                            }
                        }

                        // 发布事件（如果有）
                        try {
                            if (streamMessagePublisher != null) {
                                streamMessagePublisher.doStuffAndPublishAnEvent(null, role, taskInfo, taskInfo != null ? taskInfo.getTraceBusId() : null);
                            }
                        } catch (Exception pubEx) {
                            // 发布异常不应阻止完成流程，但记录日志
                            log.warn("publish event error", pubEx);
                        }

                        // 幂等完成 future
                        if (completed.compareAndSet(false, true)) {
                            // 取消超时任务
                            timeoutHandle.cancel(false);
                            future.complete(outputRef.get());
                        }
                    } else {
                        // 非结束消息：流中间状态，也可以选择每次都发布事件以便前端流式渲染
                        if (streamMessagePublisher != null) {
                            try {
                                streamMessagePublisher.doStuffAndPublishAnEvent(null, role, taskInfo, taskInfo != null ? taskInfo.getTraceBusId() : null);
                            } catch (Exception pubEx) {
                                log.warn("publish event error for partial message", pubEx);
                            }
                        }
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