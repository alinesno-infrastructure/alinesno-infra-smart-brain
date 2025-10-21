// DeepSearchTaskRecordManager.java (重构后)
package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record;

import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    // 顺序号生成器（taskId -> 自增序号）
    private final java.util.concurrent.ConcurrentHashMap<Long, AtomicInteger> seqGenerator = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * 获取下一个顺序号
     */
    private int getNextSeq(Long taskId) {
        return seqGenerator.computeIfAbsent(taskId, k -> new AtomicInteger(1)).getAndIncrement();
    }

    /**
     * 发布事件公共方法
     */
    private void publishEvent(DeepSearchTaskEvent event) {
        try {
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("发布深度搜索任务事件失败: {}", event, e);
        }
    }

    // ------------------------------ 事件发布方法 ------------------------------

    /**
     * 创建任务元记录
     */
    public void createTaskMeta(Long taskId, Long sceneId, String goal, String flowId) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.TASK_CREATE)
                .seq(getNextSeq(taskId))
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加任务规划
     */
    public void addTaskPlan(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Plan plan) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.PLAN_CREATE)
                .seq(getNextSeq(taskId))
                .plan(plan)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加任务规划步骤
     */
    public void addTaskPlanStep(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Plan plan) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.PLAN_STEP)
                .seq(getNextSeq(taskId))
                .plan(plan)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加工作步骤
     */
    public void addTaskWorkerStep(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Step step) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.WORKER_STEP)
                .seq(getNextSeq(taskId))
                .step(step)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加工作步骤动作
     */
    public void addTaskWorkerStepActionAsync(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Step step) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.WORKER_STEP_ACTION)
                .seq(getNextSeq(taskId))
                .step(step)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加输出创建记录
     */
    public void addTaskOutput(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Output output) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.OUTPUT_CREATE)
                .seq(getNextSeq(taskId))
                .output(output)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 添加输出步骤
     */
    public void addTaskOutputStep(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Output output) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.OUTPUT_STEP)
                .seq(getNextSeq(taskId))
                .output(output)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 更新任务状态
     */
    public void updateTaskStatus(Long taskId, String status) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .eventType(DeepSearchTaskEvent.Type.STATUS_UPDATE)
                .seq(getNextSeq(taskId))
                .status(status)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 更新任务进度
     */
    public void updateTaskProgress(Long taskId, Integer progress) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .eventType(DeepSearchTaskEvent.Type.PROGRESS_UPDATE)
                .seq(getNextSeq(taskId))
                .progress(progress)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public void addTaskWorkerStepActionSingleAsync(Long taskId, long sceneId, String goal, String flowId, DeepSearchFlow.StepAction stepActionDto , DeepSearchFlow.Step step) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.WORKER_STEP_ACTION_SINGLE)
                .step(step)
                .seq(getNextSeq(taskId))
                .stepAction(stepActionDto)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public void addTaskPlanSingleStep(Long taskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Plan plan, DeepSearchFlow.StepAction stepActionDto) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.PLAN_STEP_SINGLE)
                .seq(getNextSeq(taskId))
                .plan(plan)
                .timestamp(System.currentTimeMillis())
                .stepAction(stepActionDto)
                .build());
    }

    public void markTaskPlanSingleStep(DeepSearchFlow.StepAction stepActionDto, String markStatus) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.PLAN_STEP_SINGLE_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .stepAction(stepActionDto)
                .build());
    }

    public void markTaskPlan(DeepSearchFlow.Plan plan , String markStatus) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.PLAN_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .plan(plan)
                .build());
    }

    public void markTaskWorkerSingleStep(DeepSearchFlow.StepAction stepActionDto, String markStatus) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.WORKER_STEP_SINGLE_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .stepAction(stepActionDto)
                .build());
    }

    /**
     * 标记任务步骤
     * @param step
     */
    public void markTaskWorker(DeepSearchFlow.Step step , String markStatus) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.WORKER_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .step(step)
                .build());
    }

    /**
     * 添加输出步骤
     * @param taskId
     * @param sceneId
     * @param goal
     * @param flowId
     * @param stepActionDto
     */
    public void addTaskOutputStepSingle(Long taskId, long sceneId, String goal, String flowId, DeepSearchFlow.StepAction stepActionDto) {
        publishEvent(DeepSearchTaskEvent.builder()
                .taskId(taskId)
                .sceneId(sceneId)
                .flowId(flowId)
                .goal(goal)
                .eventType(DeepSearchTaskEvent.Type.OUTPUT_STEP_SINGLE)
                .seq(getNextSeq(taskId))
                .stepAction(stepActionDto)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    /**
     * 标记输出步骤
     * @param stepActionDto
     * @param markStatus
     */
    public void markTaskOutputStepSingle(DeepSearchFlow.StepAction stepActionDto, String markStatus) {
        this.markTaskWorkerSingleStep(stepActionDto , markStatus);
    }

    /**
     * 标记输出
     * @param deepSearchOutput
     * @param markStatus
     */
    public void markTaskOutput(DeepSearchFlow.Output deepSearchOutput, String markStatus) {
        publishEvent(DeepSearchTaskEvent.builder()
                .eventType(DeepSearchTaskEvent.Type.OUTPUT_MARK)
                .timestamp(System.currentTimeMillis())
                .status(markStatus)
                .output(deepSearchOutput)
                .build());
    }
}