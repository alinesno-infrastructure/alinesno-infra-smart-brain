// DeepSearchTaskEventListener.java
package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskRecordService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
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
                case PLAN_MARK -> handlePlanMark(event, now);
                case PLAN_STEP_SINGLE -> handlePlanStepSingle(event, now);
                case PLAN_STEP_SINGLE_MARK -> handlePlanStepSingleMark(event, now);
                case WORKER_STEP -> handleWorkerStep(event, now);
                case WORKER_MARK -> handleWorkerStepMark(event, now);
                case WORKER_STEP_ACTION -> handleWorkerStepAction(event, now);
                case WORKER_STEP_ACTION_SINGLE -> handleWorkerStepActionSingle(event, now);
                case WORKER_STEP_SINGLE_MARK -> handleWorkerStepActionSingleMark(event, now);
                case OUTPUT_CREATE -> handleOutputCreate(event, now);
                case OUTPUT_STEP -> handleOutputStep(event, now);
                case OUTPUT_MARK -> handleOutputMark(event, now);
                case OUTPUT_STEP_SINGLE -> handleOutputStepSingle(event, now);
                case STATUS_UPDATE -> handleStatusUpdate(event, now);
                case PROGRESS_UPDATE -> handleProgressUpdate(event, now);
                default -> log.warn("未知事件类型: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("处理事件失败: {}", event, e);
        }
    }

    /**
     * 处理输出标记
     * @param event
     * @param now
     */
    private void handleOutputMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Output output = event.getOutput();
        if (output == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getStepType, "OUTPUT")
                        .eq(DeepSearchTaskRecordEntity::getOutputId, output.getId())) ;

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setUpdateTime(now);

            taskRecordService.update(entity);
        }
    }

    /**
     * 处理输出步骤
     * @param event
     * @param now
     */
    private void handleOutputStepSingle(DeepSearchTaskEvent event, Date now) {

        DeepSearchFlow.StepAction action = event.getStepAction() ;

        if (action == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("OUTPUT_STEP");

        entity.setStepId(action.getActionId());
        entity.setActionId(action.getActionId());
        entity.setActionName(action.getActionName());
        entity.setThink(action.getThink());
        entity.setResult(action.getResult());
        entity.setStatus(action.getStatus());
        entity.setSeq(event.getSeq());

        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());

        taskRecordService.save(entity);
    }

    /**
     * 处理任务创建
     * @param event
     * @param now
     */
    private void handleWorkerStepMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Step step = event.getStep();

        if (step == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getActionId, "WORKER_STEP_" + step.getId())
                        .eq(DeepSearchTaskRecordEntity::getStepId, step.getId())
        ) ;

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setUpdateTime(now);

            taskRecordService.update(entity);
        }
    }

    /**
     * 单独处理单个步骤
     * @param event
     * @param now
     */
    private void handleWorkerStepActionSingleMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.StepAction stepAction = event.getStepAction();

        if (stepAction == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getActionId, stepAction.getActionId())
        ) ;

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setThink(stepAction.getThink());
            entity.setResult(stepAction.getResult());
            entity.setUpdateTime(now);

            taskRecordService.update(entity);
        }
    }

    /**
     * 处理计划标记
     * @param event
     * @param now
     */
    private void handlePlanMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Plan plan = event.getPlan();
        if (plan == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getStepType, "PLAN")
                        .eq(DeepSearchTaskRecordEntity::getPlanId, plan.getId())) ;

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setUpdateTime(now);

            taskRecordService.update(entity);
        }
    }

    /**
     * 处理计划步骤标记
     * @param event
     * @param now
     */
    private void handlePlanStepSingleMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.StepAction stepAction = event.getStepAction();

        if (stepAction == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getStepId, stepAction.getActionId())
        ) ;

        if (entity != null) {
           entity.setStatus(event.getStatus());
           entity.setThink(stepAction.getThink());
           entity.setResult(stepAction.getResult());
           entity.setUpdateTime(now);

            taskRecordService.update(entity);
        }

    }

    /**
     * 单独处理单个步骤
     * @param event
     * @param now
     */
    private void handleWorkerStepActionSingle(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.StepAction stepAction = event.getStepAction();
        DeepSearchFlow.Step step = event.getStep();

        if (stepAction == null || step == null){
            log.error("处理任务创建失败，参数错误");
            return;
        }

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());

        entity.setStepId(step.getId());
        entity.setStepType("WORKER_ACTION");
        entity.setActionId(stepAction.getActionId());
        entity.setActionName(stepAction.getActionName());
        entity.setThink(stepAction.getThink());
        entity.setResult(stepAction.getResult());
        entity.setStatus(stepAction.getStatus());
        entity.setIcon(stepAction.getIcon());

        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());

        taskRecordService.save(entity);
    }

    /**
     * 处理任务创建
     * @param event
     * @param now
     */
    private void handleTaskCreate(DeepSearchTaskEvent event, Date now) {
        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();

        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("TASK");
        entity.setActionId("TASK_META");
        entity.setStatus(StepActionStatusEnums.DOING.getKey());
        entity.setProgress(0);
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setStartTime(now);
        entity.setTimestamp(event.getTimestamp());

        taskRecordService.save(entity);
    }

    /**
     * 处理计划创建
     * @param event
     * @param now
     */
    private void handlePlanCreate(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Plan plan = event.getPlan();
        if (plan == null) return;

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();

        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());

        entity.setPlanId(plan.getId());
        entity.setStepType("PLAN");
        entity.setActionId(plan.getId());
        entity.setActionName(plan.getName());
        entity.setResult(plan.getDescription());

        entity.setStatus(StepActionStatusEnums.DOING.getKey());
        entity.setSeq(event.getSeq());

        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());

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
            entity.setIcon(action.getIcon());

            entity.setSeq(event.getSeq()); // 同批次步骤使用相同顺序号
            entity.setAddTime(now);
            entity.setUpdateTime(now);
            entity.setTimestamp(event.getTimestamp());

            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            taskRecordService.saveBatch(entities);
        }
    }

    private void handlePlanStepSingle(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Plan plan = event.getPlan();
        DeepSearchFlow.StepAction stepAction = event.getStepAction();

        if (plan == null || stepAction == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();

        entity.setPlanId(plan.getId());
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());

        entity.setStepType("PLAN_STEP");
        entity.setStepId(stepAction.getActionId());
        entity.setActionId(stepAction.getActionId());
        entity.setActionName(stepAction.getActionName());
        entity.setThink(stepAction.getThink());
        entity.setResult(stepAction.getResult());
        entity.setStatus(stepAction.getStatus());
        entity.setIcon(stepAction.getIcon());

        entity.setSeq(event.getSeq()); // 同批次步骤使用相同顺序号
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());

        taskRecordService.save(entity);
    }

    private void handleWorkerStep(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Step step = event.getStep();
        if (step == null) return;

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());

        entity.setStepId(step.getId());
        entity.setStepType("WORKER");
        entity.setActionId("WORKER_STEP_" + step.getId());
        entity.setActionName(step.getName());
        entity.setResult(step.getDescription());
        entity.setStatus(StepActionStatusEnums.DOING.getKey());

        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
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
            entity.setIcon(action.getIcon());

            entity.setSeq(event.getSeq());
            entity.setAddTime(now);
            entity.setUpdateTime(now);
            entity.setTimestamp(event.getTimestamp());
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

        entity.setOutputId(output.getId());
        entity.setStepType("OUTPUT");
        entity.setActionId("OUTPUT_META");
        entity.setActionName(output.getName());
        entity.setResult(output.getDescription());
        entity.setStatus(StepActionStatusEnums.DOING.getKey());
        entity.setSeq(event.getSeq());

        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
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
            entity.setTimestamp(event.getTimestamp());

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