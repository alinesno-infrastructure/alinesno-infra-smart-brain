// DeepSearchTaskEventListener.java
package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskRecordService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskRecordEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 深度搜索任务事件监听器（负责持久化）
 *
 * 说明：
 * - 事件异步处理，使用 chatThreadPool 执行（确保 application 中已定义该线程池）
 * - 事件包含 seq 字段（由发布方生成自增 seq），以保证写入记录时可以按 seq 排序
 * - 监听器尽量幂等：如果需要可根据 (taskId, actionId) 做去重或更新策略
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSearchTaskEventListener {

    private final IDeepSearchTaskRecordService taskRecordService;

    @Async("chatThreadPool")
    @EventListener(DeepSearchTaskEvent.class)
    public void handleDeepSearchTaskEvent(DeepSearchTaskEvent event) {
        if (event == null) {
            return;
        }

        log.debug("处理深度搜索任务事件: taskId={}, type={}, seq={}", event.getTaskId(), event.getEventType(), event.getSeq());
        log.debug("事件内容: {}", JSONObject.toJSONString(event));

        Date now = new Date();

        try {
            switch (event.getEventType()) {
                case TASK_CREATE -> handleTaskCreate(event, now);
                case PLAN_CREATE -> handlePlanCreate(event, now);
                case PLAN_STEP -> handlePlanStep(event, now);
                case WORKER_STEP -> handleWorkerStep(event, now);
                case WORKER_STEP_ACTION -> handleWorkerStepAction(event, now);
                case OUTPUT_CREATE -> handleOutputCreate(event, now);
                case OUTPUT_STEP -> handleOutputStep(event, now);
                case STATUS_UPDATE -> handleStatusUpdate(event, now);
                case PROGRESS_UPDATE -> handleProgressUpdate(event, now);
                default -> log.warn("未知事件类型: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("处理事件失败: {}", event, e);
        }
    }

    private void handleTaskCreate(DeepSearchTaskEvent event, Date now) {
        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("TASK");
        entity.setActionId("TASK_META");
        entity.setStatus("RUNNING");
        entity.setProgress(0);
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setStartTime(now);
        taskRecordService.save(entity);
    }

    private void handlePlanCreate(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Plan plan = event.getPlan();
        if (plan == null) return;

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("PLAN");
        entity.setActionId("PLAN_META");
        entity.setActionName(plan.getName());
        entity.setResult(plan.getDescription());
        entity.setStatus("RUNNING");
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        taskRecordService.save(entity);
    }

    private void handlePlanStep(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Plan plan = event.getPlan();
        if (plan == null || plan.getActions() == null || plan.getActions().isEmpty()) return;

        List<DeepSearchTaskRecordEntity> entities = new ArrayList<>();
        for (DeepSearchFlow.StepAction action : plan.getActions()) {
            DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
            entity.setTaskId(event.getTaskId());
            entity.setSceneId(event.getSceneId());
            entity.setFlowId(event.getFlowId());
            entity.setGoal(event.getGoal());
            entity.setStepType("PLAN_STEP");
            entity.setStepId(action.getActionId());
            entity.setActionId(action.getActionId());
            entity.setActionName(action.getActionName());
            entity.setThink(action.getThink());
            entity.setResult(action.getResult());
            entity.setStatus(action.getStatus());
            entity.setSeq(event.getSeq()); // 同批次步骤使用相同顺序号
            entity.setAddTime(now);
            entity.setUpdateTime(now);
            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            taskRecordService.saveBatch(entities);
        }
    }

    private void handleWorkerStep(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Step step = event.getStep();
        if (step == null) return;

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("WORKER");
        entity.setActionId("WORKER_STEP_" + step.getId());
        entity.setActionName(step.getName());
        entity.setResult(step.getDescription());
        entity.setStatus("RUNNING");
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        taskRecordService.save(entity);
    }

    private void handleWorkerStepAction(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Step step = event.getStep();
        if (step == null || step.getActions() == null || step.getActions().isEmpty()) return;

        List<DeepSearchTaskRecordEntity> entities = new ArrayList<>();
        for (DeepSearchFlow.StepAction action : step.getActions()) {
            DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
            entity.setTaskId(event.getTaskId());
            entity.setSceneId(event.getSceneId());
            entity.setFlowId(event.getFlowId());
            entity.setGoal(event.getGoal());
            entity.setStepType("WORKER_ACTION");
            entity.setStepId(step.getId());
            entity.setActionId(action.getActionId());
            entity.setActionName(action.getActionName());
            entity.setThink(action.getThink());
            entity.setResult(action.getResult());
            entity.setStatus(action.getStatus());
            entity.setSeq(event.getSeq());
            entity.setAddTime(now);
            entity.setUpdateTime(now);
            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            taskRecordService.saveBatch(entities);
        }
    }

    private void handleOutputCreate(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Output output = event.getOutput();
        if (output == null) return;

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("OUTPUT");
        entity.setActionId("OUTPUT_META");
        entity.setActionName(output.getName());
        entity.setResult(output.getDescription());
        entity.setStatus("RUNNING");
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        taskRecordService.save(entity);
    }

    private void handleOutputStep(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Output output = event.getOutput();
        if (output == null || output.getActions() == null || output.getActions().isEmpty()) return;

        List<DeepSearchTaskRecordEntity> entities = new ArrayList<>();
        for (DeepSearchFlow.StepAction action : output.getActions()) {
            DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
            entity.setTaskId(event.getTaskId());
            entity.setSceneId(event.getSceneId());
            entity.setFlowId(event.getFlowId());
            entity.setGoal(event.getGoal());
            entity.setStepType("OUTPUT_STEP");
            entity.setActionId(action.getActionId());
            entity.setActionName(action.getActionName());
            entity.setThink(action.getThink());
            entity.setResult(action.getResult());
            entity.setStatus(action.getStatus());
            entity.setSeq(event.getSeq());
            entity.setAddTime(now);
            entity.setUpdateTime(now);
            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            taskRecordService.saveBatch(entities);
        }
    }

    private void handleStatusUpdate(DeepSearchTaskEvent event, Date now) {
        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getTaskId , event.getTaskId())
                        .eq(DeepSearchTaskRecordEntity::getStepType , "TASK")
                        .last("limit 1")
        );
        if (entity == null) {
            log.warn("任务元记录不存在: taskId={}", event.getTaskId());
            return;
        }
        entity.setStatus(event.getStatus());
        entity.setUpdateTime(now);
        if ("COMPLETED".equals(event.getStatus()) || "FAILED".equals(event.getStatus())) {
            entity.setEndTime(now);
            entity.setProgress(100); // 任务结束时强制进度为100%
        }
        taskRecordService.updateById(entity);
    }

    private void handleProgressUpdate(DeepSearchTaskEvent event, Date now) {
        if (event.getProgress() == null) return;

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getTaskId, event.getTaskId())
                        .eq(DeepSearchTaskRecordEntity::getStepType, "TASK")
                        .last("limit 1")
        );
        if (entity == null) {
            log.warn("任务元记录不存在: taskId={}", event.getTaskId());
            return;
        }
        // 进度值边界控制（0-100）
        int progress = Math.min(100, Math.max(0, event.getProgress()));
        entity.setProgress(progress);
        entity.setUpdateTime(now);
        taskRecordService.updateById(entity);
    }

}