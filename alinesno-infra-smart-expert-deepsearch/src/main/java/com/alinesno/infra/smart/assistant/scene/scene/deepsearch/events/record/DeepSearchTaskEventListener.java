package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskRecordService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
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
 * 深度搜索任务事件监听器（负责持久化，已补充所有sessionId处理）
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

        log.debug("处理深度搜索任务事件: taskId={}, type={}, seq={}, sessionId={}",
                event.getTaskId(), event.getEventType(), event.getSeq(), event.getSessionId());
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
                case ATTACHMENT_CREATE -> handleAttachmentCreate(event, now);
                case USER_ATTACHMENT_CREATE -> handleUserAttachmentCreate(event, now);
                case USER_QUESTION -> handleUserQuestion(event, now);
                default -> log.warn("未知事件类型: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("处理事件失败: {}", event, e);
        }
    }

    /**
     * 处理用户附件创建
     * @param event
     * @param now
     */
    private void handleUserAttachmentCreate(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.FileAttachmentDto attachment = event.getAttachment();
        if (attachment == null) {
            log.warn("用户附件信息为空，跳过持久化");
            return;
        }

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("USER_ATTACHMENT");
        entity.setStepId(attachment.getId());
        entity.setActionId("ATTACH_" + attachment.getId());
        entity.setActionName(attachment.getName());
        entity.setResult(JSON.toJSONString(attachment));
        entity.setStatus(StepActionStatusEnums.DONE.getKey());
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        taskRecordService.save(entity);
    }

    /**
     * 处理用户问题记录
     */
    private void handleUserQuestion(DeepSearchTaskEvent event, Date now) {
        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("USER_QUESTION");
        entity.setActionId("USER_" + event.getSessionId());
        entity.setActionName("用户问题");
        entity.setResult(event.getGoal());
        entity.setStatus(StepActionStatusEnums.DONE.getKey());
        entity.setSeq(event.getSeq());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        taskRecordService.save(entity);
    }

    /**
     * 处理输出标记
     */
    private void handleOutputMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Output output = event.getOutput();
        if (output == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getStepType, "OUTPUT")
                        .eq(DeepSearchTaskRecordEntity::getOutputId, output.getId())
        );

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setSessionId(event.getSessionId()); // 更新sessionId
            entity.setUpdateTime(now);
            taskRecordService.update(entity);
        }
    }

    /**
     * 处理输出步骤
     */
    private void handleOutputStepSingle(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.StepAction action = event.getStepAction();
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
        entity.setActionType(action.getActionType());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        taskRecordService.save(entity);
    }

    /**
     * 处理工作步骤标记
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
        );

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setSessionId(event.getSessionId()); // 更新sessionId
            entity.setUpdateTime(now);
            taskRecordService.update(entity);
        }
    }

    /**
     * 处理工作步骤动作标记
     */
    private void handleWorkerStepActionSingleMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.StepAction stepAction = event.getStepAction();
        if (stepAction == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getActionId, stepAction.getActionId())
        );

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setThink(stepAction.getThink());
            entity.setResult(stepAction.getResult());
            entity.setSessionId(event.getSessionId()); // 更新sessionId
            entity.setUpdateTime(now);
            taskRecordService.update(entity);
        }
    }

    /**
     * 处理计划标记
     */
    private void handlePlanMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.Plan plan = event.getPlan();
        if (plan == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getStepType, "PLAN")
                        .eq(DeepSearchTaskRecordEntity::getPlanId, plan.getId())
        );

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setSessionId(event.getSessionId()); // 更新sessionId
            entity.setUpdateTime(now);
            taskRecordService.update(entity);
        }
    }

    /**
     * 处理计划步骤标记
     */
    private void handlePlanStepSingleMark(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.StepAction stepAction = event.getStepAction();
        if (stepAction == null) {
            return;
        }

        DeepSearchTaskRecordEntity entity = taskRecordService.getOne(
                new LambdaQueryWrapper<DeepSearchTaskRecordEntity>()
                        .eq(DeepSearchTaskRecordEntity::getStepId, stepAction.getActionId())
        );

        if (entity != null) {
            entity.setStatus(event.getStatus());
            entity.setThink(stepAction.getThink());
            entity.setResult(stepAction.getResult());
            entity.setSessionId(event.getSessionId()); // 更新sessionId
            entity.setUpdateTime(now);
            taskRecordService.update(entity);
        }
    }

    /**
     * 处理单个工作步骤动作
     */
    private void handleWorkerStepActionSingle(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.StepAction stepAction = event.getStepAction();
        DeepSearchFlow.Step step = event.getStep();
        if (stepAction == null || step == null) {
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
        entity.setActionType(stepAction.getActionType());
        entity.setSeq(event.getSeq());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        taskRecordService.save(entity);
    }

    /**
     * 处理任务创建
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
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        taskRecordService.save(entity);
    }

    /**
     * 处理计划创建
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
        entity.setActionType(StepActionEnums.PLAN.getActionType());
        entity.setStatus(StepActionStatusEnums.DOING.getKey());
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        taskRecordService.save(entity);
    }

    /**
     * 处理计划步骤
     */
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
            entity.setActionType(action.getActionType());
            entity.setSeq(event.getSeq());
            entity.setAddTime(now);
            entity.setUpdateTime(now);
            entity.setTimestamp(event.getTimestamp());
            entity.setSessionId(event.getSessionId()); // 存储sessionId
            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            taskRecordService.saveBatch(entities);
        }
    }

    /**
     * 处理单个计划步骤
     */
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
        entity.setActionType(stepAction.getActionType());
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        taskRecordService.save(entity);
    }

    /**
     * 处理工作步骤
     */
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
        entity.setActionType(StepActionEnums.PLAN.getActionType());
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        taskRecordService.save(entity);
    }

    /**
     * 处理工作步骤动作
     */
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
            entity.setActionType(action.getActionType());
            entity.setSeq(event.getSeq());
            entity.setAddTime(now);
            entity.setUpdateTime(now);
            entity.setTimestamp(event.getTimestamp());
            entity.setSessionId(event.getSessionId()); // 存储sessionId
            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            taskRecordService.saveBatch(entities);
        }
    }

    /**
     * 处理输出创建
     */
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
        entity.setActionType(StepActionEnums.SUMMARY.getActionType());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        taskRecordService.save(entity);
    }

    /**
     * 处理输出步骤
     */
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
            entity.setActionType(action.getActionType());
            entity.setSeq(event.getSeq());
            entity.setAddTime(now);
            entity.setUpdateTime(now);
            entity.setTimestamp(event.getTimestamp());
            entity.setSessionId(event.getSessionId()); // 存储sessionId
            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            taskRecordService.saveBatch(entities);
        }
    }

    /**
     * 处理状态更新
     */
    private void handleStatusUpdate(DeepSearchTaskEvent event, Date now) {
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
        entity.setStatus(event.getStatus());
        entity.setSessionId(event.getSessionId()); // 更新sessionId
        entity.setUpdateTime(now);
        if ("COMPLETED".equals(event.getStatus()) || "FAILED".equals(event.getStatus())) {
            entity.setEndTime(now);
            entity.setProgress(100);
        }
        taskRecordService.updateById(entity);
    }

    /**
     * 处理进度更新
     */
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
        int progress = Math.min(100, Math.max(0, event.getProgress()));
        entity.setProgress(progress);
        entity.setSessionId(event.getSessionId()); // 更新sessionId
        entity.setUpdateTime(now);
        taskRecordService.updateById(entity);
    }

    /**
     * 处理附件创建事件
     */
    private void handleAttachmentCreate(DeepSearchTaskEvent event, Date now) {
        DeepSearchFlow.FileAttachmentDto attachment = event.getAttachment();
        if (attachment == null) {
            log.warn("附件信息为空，跳过持久化");
            return;
        }

        DeepSearchTaskRecordEntity entity = new DeepSearchTaskRecordEntity();
        entity.setTaskId(event.getTaskId());
        entity.setSceneId(event.getSceneId());
        entity.setFlowId(event.getFlowId());
        entity.setGoal(event.getGoal());
        entity.setStepType("ATTACHMENT");
        entity.setStepId(attachment.getId());
        entity.setActionId("ATTACH_" + attachment.getId());
        entity.setActionName(attachment.getName());
        entity.setResult(JSON.toJSONString(attachment));
        entity.setStatus(StepActionStatusEnums.DONE.getKey());
        entity.setSeq(event.getSeq());
        entity.setAddTime(now);
        entity.setUpdateTime(now);
        entity.setTimestamp(event.getTimestamp());
        entity.setSessionId(event.getSessionId()); // 存储sessionId
        taskRecordService.save(entity);
    }
}