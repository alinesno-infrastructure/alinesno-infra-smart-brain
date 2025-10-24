// DeepSearchTaskRecordManager.java (重构后)
package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 深度搜索任务记录管理器（事件驱动版）
 */
@Slf4j
@Scope("prototype")
@Component
@RequiredArgsConstructor
public class DeepSearchTaskRecordManager {

    private final ApplicationEventPublisher eventPublisher;
    private final ConcurrentHashMap<Long, AtomicInteger> seqGenerator = new ConcurrentHashMap<>();

    private int getNextSeq(Long taskId) {
        return seqGenerator.computeIfAbsent(taskId, k -> new AtomicInteger(1)).getAndIncrement();
    }

    private void publishEvent(DeepSearchTaskEvent event) {
        try {
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("发布深度搜索任务事件失败: {}", event, e);
        }
    }

    // ------------------------------ 事件发布方法（均补充sessionId） ------------------------------

    /**
     * 创建任务元记录
     */
    public void createTaskMeta(Long taskId, Long sceneId, String goal, String flowId, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.TASK_CREATE)
                .seq(getNextSeq(taskId))
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加任务规划
     */
    public void addTaskPlan(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Plan plan, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.PLAN_CREATE)
                .seq(getNextSeq(taskId))
                .plan(plan)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加任务规划步骤
     */
    public void addTaskPlanStep(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Plan plan, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.PLAN_STEP)
                .seq(getNextSeq(taskId))
                .plan(plan)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加工作步骤
     */
    public void addTaskWorkerStep(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Step step, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.WORKER_STEP)
                .seq(getNextSeq(taskId))
                .step(step)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加工作步骤动作
     */
    public void addTaskWorkerStepActionAsync(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Step step, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.WORKER_STEP_ACTION)
                .seq(getNextSeq(taskId))
                .step(step)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加输出创建记录
     */
    public void addTaskOutput(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Output output, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.OUTPUT_CREATE)
                .seq(getNextSeq(taskId))
                .output(output)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加输出步骤
     */
    public void addTaskOutputStep(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Output output, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.OUTPUT_STEP)
                .seq(getNextSeq(taskId))
                .output(output)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 更新任务状态
     */
    public void updateTaskStatus(Long taskId, String status, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .eventType(DeepSearchTaskEvent.Type.STATUS_UPDATE)
                .seq(getNextSeq(taskId))
                .status(status)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 更新任务进度
     */
    public void updateTaskProgress(Long taskId, Integer progress, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .eventType(DeepSearchTaskEvent.Type.PROGRESS_UPDATE)
                .seq(getNextSeq(taskId))
                .progress(progress)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public void addTaskWorkerStepActionSingleAsync(Long taskId, long sceneId, String goal, String flowId, DeepSearchFlow.StepAction stepActionDto, DeepSearchFlow.Step step, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.WORKER_STEP_ACTION_SINGLE)
                .step(step)
                .seq(getNextSeq(taskId))
                .stepAction(stepActionDto)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public void addTaskPlanSingleStep(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Plan plan, DeepSearchFlow.StepAction stepActionDto, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.PLAN_STEP_SINGLE)
                .seq(getNextSeq(taskId))
                .plan(plan)
                .stepAction(stepActionDto)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public void markTaskPlanSingleStep(DeepSearchFlow.StepAction stepActionDto, String markStatus, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.PLAN_STEP_SINGLE_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .stepAction(stepActionDto)
                .sessionId(sessionId) // 新增sessionId
                .build());
    }

    public void markTaskPlan(DeepSearchFlow.Plan plan, String markStatus, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.PLAN_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .plan(plan)
                .sessionId(sessionId) // 新增sessionId
                .build());
    }

    public void markTaskWorkerSingleStep(DeepSearchFlow.StepAction stepActionDto, String markStatus, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.WORKER_STEP_SINGLE_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .stepAction(stepActionDto)
                .sessionId(sessionId) // 新增sessionId
                .build());
    }

    /**
     * 标记任务步骤
     */
    public void markTaskWorker(DeepSearchFlow.Step step, String markStatus, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.WORKER_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .step(step)
                .sessionId(sessionId) // 新增sessionId
                .build());
    }

    /**
     * 添加输出步骤
     */
    public void addTaskOutputStepSingle(Long taskId, long sceneId, String goal, String flowId, DeepSearchFlow.StepAction stepActionDto, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.OUTPUT_STEP_SINGLE)
                .seq(getNextSeq(taskId))
                .stepAction(stepActionDto)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 标记输出步骤
     */
    public void markTaskOutputStepSingle(DeepSearchFlow.StepAction stepActionDto, String markStatus, String sessionId) {
        this.markTaskWorkerSingleStep(stepActionDto, markStatus, sessionId); // 传递sessionId
    }

    /**
     * 标记输出
     */
    public void markTaskOutput(DeepSearchFlow.Output deepSearchOutput, String markStatus, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.OUTPUT_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .output(deepSearchOutput)
                .sessionId(sessionId) // 新增sessionId
                .build());
    }

    /**
     * 发布附件创建事件
     */
    public void publishAttachmentEvent(Long taskId, long sceneId, String goal, String flowId, DeepSearchFlow.FileAttachmentDto attachment, String sessionId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.ATTACHMENT_CREATE)
                .seq(getNextSeq(taskId))
                .attachment(attachment)
                .sessionId(sessionId) // 新增sessionId
                .timestamp(System.currentTimeMillis())
                .build());
    }


    /**
     * 记录用户问题（主记录）
     */
    public void addUserQuestion(Long taskId, Long sceneId, String goal, String sessionId) {
        String flowId = IdUtil.getSnowflakeNextIdStr(); // 生成独立的flowId，不依赖deepSearchFlow
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.USER_QUESTION)
                .seq(getNextSeq(taskId))
                .sessionId(sessionId)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 记录用户问题的附件
     */
    public void addUserQuestionAttachments(Long taskId, Long sceneId, String goal, String sessionId, List<DeepSearchFlow.FileAttachmentDto> attachments) {
        String flowId = IdUtil.getSnowflakeNextIdStr();
        for (DeepSearchFlow.FileAttachmentDto attachment : attachments) {
            publishEvent(DeepSearchTaskEvent.builder()
                    .taskId(taskId)
                    .sceneId(sceneId)
                    .flowId(flowId)
                    .goal(goal)
                    .eventType(DeepSearchTaskEvent.Type.USER_ATTACHMENT_CREATE)
                    .seq(getNextSeq(taskId))
                    .sessionId(sessionId)
                    .attachment(attachment)
                    .timestamp(System.currentTimeMillis())
                    .build());
        }
    }
}