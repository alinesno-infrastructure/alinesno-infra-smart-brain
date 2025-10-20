package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;

import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskStatusService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskStatusEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 管理 deep_search_task_status 表（已合并）
 * - TASK 行作为任务元信息
 * - PLAN/STEP/OUTPUT 行作为每个动作记录（action）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSearchTaskStatusManager {

    private final IDeepSearchTaskStatusService taskStatusService;
    private final ThreadPoolTaskExecutor chatThreadPool;

    // 防抖：每个 taskId 的上次持久化时间（ms）
    private final ConcurrentHashMap<Long, AtomicLong> lastPersistTimeMap = new ConcurrentHashMap<>();

    private static final String TASK_META_ACTION_ID = "TASK_META";

    /**
     * 创建任务元记录（step_type = TASK）
     */
    public void createTaskMeta(Long taskId, Long sceneId, String goal, String flowId) {
        DeepSearchTaskStatusEntity meta = new DeepSearchTaskStatusEntity();
        meta.setTaskId(taskId);
        meta.setSceneId(sceneId);
        meta.setGoal(goal);
        meta.setFlowId(flowId);
        meta.setStepType("TASK");
        meta.setActionId(TASK_META_ACTION_ID);
        meta.setStatus("RUNNING");
        meta.setProgress(0);
        Date now = new Date();
        meta.setStartTime(now);
        meta.setAddTime(now);
        meta.setUpdateTime(now);
        taskStatusService.save(meta);
    }

    /**
     * 同步 upsert 单个 action 记录（PLAN/STEP/OUTPUT）
     * - 若存在 (task_id, action_id) 则更新 think/result/status/updatedAt
     * - 否则插入新记录
     */
    public void upsertActionRecordSync(Long taskId,
                                       Long sceneId,
                                       String flowId,
                                       String stepType,
                                       String stepId,
                                       DeepSearchFlow.StepAction action,
                                       Integer seq) {
        if (action == null || action.getActionId() == null) return;
        try {
            LambdaQueryWrapper<DeepSearchTaskStatusEntity> qw = new LambdaQueryWrapper<>();
            qw.eq(DeepSearchTaskStatusEntity::getTaskId, taskId)
                    .eq(DeepSearchTaskStatusEntity::getActionId, action.getActionId())
                    .last("limit 1");

            DeepSearchTaskStatusEntity exist = taskStatusService.getOne(qw);
            Date now = new Date();
            if (exist == null) {
                DeepSearchTaskStatusEntity e = new DeepSearchTaskStatusEntity();
                e.setTaskId(taskId);
                e.setSceneId(sceneId);
                e.setFlowId(flowId);
                e.setStepType(stepType);
                e.setStepId(stepId);
                e.setActionId(action.getActionId());
                e.setActionType(action.getActionType());
                e.setActionName(action.getActionName());
                e.setThink(action.getThink());
                e.setResult(action.getResult());
                e.setStatus(action.getStatus());
                e.setSeq(seq);
                e.setExtra(null);
                e.setStorageId(null);
                e.setAddTime(now);
                e.setUpdateTime(now);
                taskStatusService.save(e);
            } else {
                // 更新策略：覆盖部分字段；若希望追加，请改为 append
                boolean changed = false;
                if (action.getThink() != null) {
                    exist.setThink(action.getThink());
                    changed = true;
                }
                if (action.getResult() != null) {
                    exist.setResult(action.getResult());
                    changed = true;
                }
                if (action.getStatus() != null) {
                    exist.setStatus(action.getStatus());
                    changed = true;
                }
                if (seq != null) {
                    exist.setSeq(seq);
                    changed = true;
                }
                if (changed) {
                    exist.setUpdateTime(now);
                    taskStatusService.updateById(exist);
                }
            }
        } catch (Exception e) {
            log.error("upsertActionRecordSync error, taskId={}, actionId={}", taskId, action.getActionId(), e);
        }
    }

    /**
     * 异步防抖 upsert（StepEventUtil 推荐使用）
     */
    public CompletableFuture<Void> upsertActionRecordAsync(Long taskId,
                                                           Long sceneId,
                                                           String flowId,
                                                           String stepType,
                                                           String stepId,
                                                           DeepSearchFlow.StepAction action,
                                                           Integer seq) {
        AtomicLong last = lastPersistTimeMap.computeIfAbsent(taskId, k -> new AtomicLong(0));
        long now = System.currentTimeMillis();
        long PERSIST_DEBOUNCE_MS = 800L;
        if (now - last.get() < PERSIST_DEBOUNCE_MS) {
            // 防抖：跳过高频写库（也可以选择延迟写入或合并）
            return CompletableFuture.completedFuture(null);
        }
        last.set(now);
        return CompletableFuture.runAsync(() -> upsertActionRecordSync(taskId, sceneId, flowId, stepType, stepId, action, seq), chatThreadPool);
    }

    /**
     * 查询指定 task 的所有动作记录（排除 TASK 元记录）
     */
    public List<DeepSearchTaskStatusEntity> listActionsByTask(Long taskId) {

        LambdaQueryWrapper<DeepSearchTaskStatusEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(DeepSearchTaskStatusEntity::getTaskId, taskId) ;
        qw.ne(DeepSearchTaskStatusEntity::getActionId, TASK_META_ACTION_ID);
        qw.orderByAsc(DeepSearchTaskStatusEntity::getAddTime) ;

        return taskStatusService.list(qw);
    }

    /**
     * 更新任务元记录状态（TASK 行）
     */
    public void updateTaskStatus(Long taskId, String status) {
        try {
            LambdaQueryWrapper<DeepSearchTaskStatusEntity> qw = new LambdaQueryWrapper<>();
            qw.eq(DeepSearchTaskStatusEntity::getTaskId, taskId)
                    .eq(DeepSearchTaskStatusEntity::getStepType, "TASK")
                    .last("limit 1");
            DeepSearchTaskStatusEntity meta = taskStatusService.getOne(qw);
            if (meta == null) {
                log.warn("updateTaskStatus: task meta not found for taskId={}, creating new meta row", taskId);
                meta = new DeepSearchTaskStatusEntity();
                meta.setTaskId(taskId);
                meta.setStepType("TASK");
                meta.setActionId(TASK_META_ACTION_ID);
                meta.setStatus(status);
                meta.setAddTime(new Date());
                meta.setUpdateTime(new Date());
                if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                    meta.setEndTime(new Date());
                }
                taskStatusService.save(meta);
                return;
            }
            meta.setStatus(status);
            meta.setUpdateTime(new Date());
            if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                meta.setEndTime(new Date());
            }
            if ("COMPLETED".equals(status)) {
                meta.setProgress(100);
            }
            taskStatusService.updateById(meta);
        } catch (Exception e) {
            log.error("updateTaskStatus error, taskId={}", taskId, e);
        }
    }

    /**
     * 更新任务进度（可按需要计算）
     */
    public void updateTaskProgress(Long taskId, Integer progress) {
        try {
            LambdaQueryWrapper<DeepSearchTaskStatusEntity> qw = new LambdaQueryWrapper<>();
            qw.eq(DeepSearchTaskStatusEntity::getTaskId, taskId)
                    .eq(DeepSearchTaskStatusEntity::getStepType, "TASK")
                    .last("limit 1");
            DeepSearchTaskStatusEntity meta = taskStatusService.getOne(qw);
            if (meta == null) {
                return;
            }
            meta.setProgress(progress);
            meta.setUpdateTime(new Date());
            taskStatusService.updateById(meta);
        } catch (Exception e) {
            log.error("updateTaskProgress error, taskId={}", taskId, e);
        }
    }
}