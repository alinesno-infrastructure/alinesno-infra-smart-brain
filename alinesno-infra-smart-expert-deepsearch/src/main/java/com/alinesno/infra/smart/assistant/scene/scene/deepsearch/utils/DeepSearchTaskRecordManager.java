//package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;
//
//import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskRecordService;
//import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
//import com.alinesno.infra.smart.scene.entity.DeepSearchTaskRecordEntity;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * 管理 deep_search_task_status 表（已合并）
// * - TASK 行作为任务元信息
// * - PLAN/STEP/OUTPUT 行作为每个动作记录（action）
// *
// * 并发改造说明：
// * - 对于对同一 taskId 的所有修改操作使用 taskLocks 的 ReentrantLock 进行保护，保证序列化访问，避免并发修改导致的 ConcurrentModificationException 或不一致。
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DeepSearchTaskRecordManager {
//
//    private final IDeepSearchTaskRecordService taskStatusService;
//    private final ThreadPoolTaskExecutor chatThreadPool;
//
//    // 防抖：每个 taskId 的上次持久化时间（ms）
//    private final ConcurrentHashMap<Long, AtomicLong> lastPersistTimeMap = new ConcurrentHashMap<>();
//
//    // taskId -> lock，用于任务粒度并发控制
//    private final ConcurrentHashMap<Long, ReentrantLock> taskLocks = new ConcurrentHashMap<>();
//
//    private static final String TASK_META_ACTION_ID = "TASK_META";
//
//    private ReentrantLock getLock(Long taskId) {
//        if (taskId == null) {
//            // fallback 全局锁 (尽量避免使用 null taskId)
//            return taskLocks.computeIfAbsent(-1L, k -> new ReentrantLock());
//        }
//        return taskLocks.computeIfAbsent(taskId, k -> new ReentrantLock());
//    }
//
//    private void cleanupLock(Long taskId) {
//        if (taskId == null) return;
//        // Remove lock if present and not held by any thread (best effort)
//        ReentrantLock lock = taskLocks.get(taskId);
//        if (lock != null && !lock.isLocked()) {
//            taskLocks.remove(taskId);
//        }
//    }
//
//    /**
//     * 创建任务元记录（step_type = TASK）
//     */
//    public void createTaskMeta(Long taskId, Long sceneId, String goal, String flowId) {
//        ReentrantLock lock = getLock(taskId);
//        lock.lock();
//        try {
//            DeepSearchTaskRecordEntity meta = new DeepSearchTaskRecordEntity();
//            meta.setTaskId(taskId);
//            meta.setSceneId(sceneId);
//            meta.setGoal(goal);
//            meta.setFlowId(flowId);
//            meta.setStepType("TASK");
//            meta.setActionId(TASK_META_ACTION_ID);
//            meta.setStatus("RUNNING");
//            meta.setProgress(0);
//            Date now = new Date();
//            meta.setStartTime(now);
//            meta.setAddTime(now);
//            meta.setUpdateTime(now);
//            taskStatusService.save(meta);
//        } catch (Exception e) {
//            log.error("createTaskMeta error, taskId={}", taskId, e);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    /**
//     * 同步 upsert 单个 action 记录（PLAN/STEP/OUTPUT）
//     * - 若存在 (task_id, action_id) 则更新 think/result/status/updatedAt
//     * - 否则插入新记录
//     *
//     * 此操作在 taskId 级别上加锁，避免并发读写冲突。
//     */
//    public void upsertActionRecordSync(Long taskId,
//                                       Long sceneId,
//                                       String flowId,
//                                       String stepType,
//                                       String stepId,
//                                       DeepSearchFlow.StepAction action,
//                                       Integer seq) {
//        if (action == null || action.getActionId() == null) {
//            log.debug("upsertActionRecordSync skip null action or actionId, taskId={}", taskId);
//            return;
//        }
//
//        ReentrantLock lock = getLock(taskId);
//        lock.lock();
//        try {
//            LambdaQueryWrapper<DeepSearchTaskRecordEntity> qw = new LambdaQueryWrapper<>();
//            qw.eq(DeepSearchTaskRecordEntity::getTaskId, taskId)
//                    .eq(DeepSearchTaskRecordEntity::getActionId, action.getActionId())
//                    .last("limit 1");
//
//            DeepSearchTaskRecordEntity exist = taskStatusService.getOne(qw);
//            Date now = new Date();
//            if (exist == null) {
//                DeepSearchTaskRecordEntity e = new DeepSearchTaskRecordEntity();
//                e.setTaskId(taskId);
//                e.setSceneId(sceneId);
//                e.setFlowId(flowId);
//                e.setStepType(stepType);
//                e.setStepId(stepId);
//                e.setActionId(action.getActionId());
//                e.setActionType(action.getActionType());
//                e.setActionName(action.getActionName());
//                e.setThink(action.getThink());
//                e.setResult(action.getResult());
//                e.setStatus(action.getStatus());
//                e.setSeq(seq);
//                e.setExtra(null);
//                e.setStorageId(null);
//                e.setAddTime(now);
//                e.setUpdateTime(now);
//                taskStatusService.save(e);
//            } else {
//                // 更新策略：覆盖部分字段；若希望追加，请改为 append
//                boolean changed = false;
//                if (action.getThink() != null) {
//                    exist.setThink(action.getThink());
//                    changed = true;
//                }
//                if (action.getResult() != null) {
//                    exist.setResult(action.getResult());
//                    changed = true;
//                }
//                if (action.getStatus() != null) {
//                    exist.setStatus(action.getStatus());
//                    changed = true;
//                }
//                if (seq != null) {
//                    exist.setSeq(seq);
//                    changed = true;
//                }
//                if (changed) {
//                    exist.setUpdateTime(now);
//                    taskStatusService.updateById(exist);
//                }
//            }
//        } catch (Exception e) {
//            log.error("upsertActionRecordSync error, taskId={}, actionId={}", taskId, action.getActionId(), e);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    /**
//     * 异步防抖 upsert（StepEventUtil 推荐使用）
//     */
//    public CompletableFuture<Void> upsertActionRecordAsync(Long taskId,
//                                                           Long sceneId,
//                                                           String flowId,
//                                                           String stepType,
//                                                           String stepId,
//                                                           DeepSearchFlow.StepAction action,
//                                                           Integer seq) {
//        AtomicLong last = lastPersistTimeMap.computeIfAbsent(taskId, k -> new AtomicLong(0));
//        long now = System.currentTimeMillis();
//        long PERSIST_DEBOUNCE_MS = 800L;
//        if (now - last.get() < PERSIST_DEBOUNCE_MS) {
//            // 防抖：跳过高频写库（也可以选择延迟写入或合并）
//            log.debug("upsertActionRecordAsync debounce skip, taskId={}", taskId);
//            return CompletableFuture.completedFuture(null);
//        }
//        last.set(now);
//        return CompletableFuture.runAsync(() -> upsertActionRecordSync(taskId, sceneId, flowId, stepType, stepId, action, seq), chatThreadPool);
//    }
//
//    /**
//     * 查询指定 task 的所有动作记录（排除 TASK 元记录）
//     */
//    public List<DeepSearchTaskRecordEntity> listActionsByTask(Long taskId) {
//        try {
//            LambdaQueryWrapper<DeepSearchTaskRecordEntity> qw = new LambdaQueryWrapper<>();
//            qw.eq(DeepSearchTaskRecordEntity::getTaskId, taskId);
//            qw.ne(DeepSearchTaskRecordEntity::getActionId, TASK_META_ACTION_ID);
//            qw.orderByAsc(DeepSearchTaskRecordEntity::getAddTime);
//
//            return taskStatusService.list(qw);
//        } catch (Exception e) {
//            log.error("listActionsByTask error, taskId={}", taskId, e);
//            return new ArrayList<>();
//        }
//    }
//
//    /**
//     * 更新任务元记录状态（TASK 行）
//     */
//    public void updateTaskStatus(Long taskId, String status) {
//        ReentrantLock lock = getLock(taskId);
//        lock.lock();
//        try {
//            LambdaQueryWrapper<DeepSearchTaskRecordEntity> qw = new LambdaQueryWrapper<>();
//            qw.eq(DeepSearchTaskRecordEntity::getTaskId, taskId)
//                    .eq(DeepSearchTaskRecordEntity::getStepType, "TASK")
//                    .last("limit 1");
//            DeepSearchTaskRecordEntity meta = taskStatusService.getOne(qw);
//            if (meta == null) {
//                log.warn("updateTaskStatus: task meta not found for taskId={}, creating new meta row", taskId);
//                meta = new DeepSearchTaskRecordEntity();
//                meta.setTaskId(taskId);
//                meta.setStepType("TASK");
//                meta.setActionId(TASK_META_ACTION_ID);
//                meta.setStatus(status);
//                meta.setAddTime(new Date());
//                meta.setUpdateTime(new Date());
//                if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
//                    meta.setEndTime(new Date());
//                }
//                taskStatusService.save(meta);
//            } else {
//                meta.setStatus(status);
//                meta.setUpdateTime(new Date());
//                if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
//                    meta.setEndTime(new Date());
//                }
//                if ("COMPLETED".equals(status)) {
//                    meta.setProgress(100);
//                }
//                taskStatusService.updateById(meta);
//            }
//        } catch (Exception e) {
//            log.error("updateTaskStatus error, taskId={}", taskId, e);
//        } finally {
//            // 如果任务结束，则尝试清理本地锁资源
//            try {
//                if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
//                    cleanupLock(taskId);
//                }
//            } catch (Exception ex) {
//                log.debug("cleanupLock error for taskId={}", taskId, ex);
//            } finally {
//                lock.unlock();
//            }
//        }
//    }
//
//    /**
//     * 更新任务进度（可按需要计算）
//     */
//    public void updateTaskProgress(Long taskId, Integer progress) {
//        ReentrantLock lock = getLock(taskId);
//        lock.lock();
//        try {
//            LambdaQueryWrapper<DeepSearchTaskRecordEntity> qw = new LambdaQueryWrapper<>();
//            qw.eq(DeepSearchTaskRecordEntity::getTaskId, taskId)
//                    .eq(DeepSearchTaskRecordEntity::getStepType, "TASK")
//                    .last("limit 1");
//            DeepSearchTaskRecordEntity meta = taskStatusService.getOne(qw);
//            if (meta == null) {
//                return;
//            }
//            meta.setProgress(progress);
//            meta.setUpdateTime(new Date());
//            taskStatusService.updateById(meta);
//        } catch (Exception e) {
//            log.error("updateTaskProgress error, taskId={}", taskId, e);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    /**
//     * 添加任务规划
//     */
//    @Async
//    public void addTaskPlan(Long sceneTaskId,
//                            Long sceneId,
//                            String goal,
//                            String flowId,
//                            DeepSearchFlow.Plan plan) {
//
//        if (plan == null) {
//            log.debug("addTaskPlan: plan is null, taskId={}", sceneTaskId);
//            return;
//        }
//
//        DeepSearchTaskRecordEntity meta = new DeepSearchTaskRecordEntity();
//        meta.setTaskId(sceneTaskId);
//        meta.setSceneId(sceneId);
//        meta.setGoal(goal);
//        meta.setFlowId(flowId);
//        meta.setStepType("PLAN");
//        meta.setStatus("RUNNING");
//        meta.setProgress(0);
//
//        meta.setActionName(plan.getName());
//        // plan 作为元数据行，这里复用 TASK_META_ACTION_ID 以示元行（旧逻辑保留）
//        meta.setActionId(TASK_META_ACTION_ID);
//        meta.setResult(plan.getDescription());
//
//        Date now = new Date();
//        meta.setStartTime(now);
//        meta.setAddTime(now);
//        meta.setUpdateTime(now);
//
//        taskStatusService.save(meta);
//    }
//
//    /**
//     * 添加任务规划步骤
//     */
//    @Async
//    public void addTaskPlanStep(Long sceneTaskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Plan plan) {
//        if (plan == null || plan.getActions() == null || plan.getActions().isEmpty()) {
//            return;
//        }
//
//        List<DeepSearchTaskRecordEntity> stepList = new ArrayList<>();
//
//        for (DeepSearchFlow.StepAction step : plan.getActions()) {
//            DeepSearchTaskRecordEntity meta = new DeepSearchTaskRecordEntity();
//            meta.setTaskId(sceneTaskId);
//            meta.setSceneId(sceneId);
//            meta.setGoal(goal);
//            meta.setFlowId(flowId);
//            meta.setStepType("STEP");
//            meta.setStatus("RUNNING");
//            meta.setProgress(0);
//
//            meta.setActionId(step.getActionId());
//            meta.setIcon(step.getIcon());
//            meta.setActionName(step.getActionName());
//            meta.setThink(step.getThink());
//            meta.setResult(step.getResult());
//            meta.setStatus(step.getStatus());
//            meta.setErrorMsg(step.getErrorMsg());
//
//            Date now = new Date();
//            meta.setStartTime(now);
//            meta.setAddTime(now);
//            meta.setUpdateTime(now);
//
//            stepList.add(meta);
//        }
//
//        if (!stepList.isEmpty()) {
//            taskStatusService.saveBatch(stepList);
//        }
//    }
//
//    public void addTaskWorkerStep(Long sceneTaskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Step step) {
//        if (step == null) {
//            log.debug("addTaskWorkerStep: step is null, taskId={}", sceneTaskId);
//            return;
//        }
//        ReentrantLock lock = getLock(sceneTaskId);
//        lock.lock();
//        try {
//            DeepSearchTaskRecordEntity meta = new DeepSearchTaskRecordEntity();
//            meta.setTaskId(sceneTaskId);
//            meta.setSceneId(sceneId);
//            meta.setGoal(goal);
//            meta.setFlowId(flowId);
//            meta.setStepType("Worker");
//            meta.setStatus("RUNNING");
//            meta.setProgress(0);
//
//            meta.setActionName(step.getName());
//            meta.setActionId(TASK_META_ACTION_ID);
//            meta.setResult(step.getDescription());
//
//            Date now = new Date();
//            meta.setStartTime(now);
//            meta.setAddTime(now);
//            meta.setUpdateTime(now);
//
//            taskStatusService.save(meta);
//        } catch (Exception e) {
//            log.error("addTaskWorkerStep error, taskId={}", sceneTaskId, e);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    @Async
//    public void addTaskWorkerStepActionAsync(Long sceneTaskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Step steps) {
//
//        if (steps == null || steps.getActions() == null || steps.getActions().isEmpty()) {
//            return;
//        }
//
//        List<DeepSearchTaskRecordEntity> stepList = new ArrayList<>();
//
//            for (DeepSearchFlow.StepAction step : steps.getActions()) {
//                DeepSearchTaskRecordEntity meta = new DeepSearchTaskRecordEntity();
//                meta.setTaskId(sceneTaskId);
//                meta.setSceneId(sceneId);
//                meta.setGoal(goal);
//                meta.setFlowId(flowId);
//                meta.setStepType("STEP");
//                meta.setStatus("RUNNING");
//                meta.setProgress(0);
//
//                meta.setActionId(step.getActionId());
//                meta.setIcon(step.getIcon());
//                meta.setActionName(step.getActionName());
//                meta.setThink(step.getThink());
//                meta.setResult(step.getResult());
//                meta.setStatus(step.getStatus());
//                meta.setErrorMsg(step.getErrorMsg());
//
//                Date now = new Date();
//                meta.setStartTime(now);
//                meta.setAddTime(now);
//                meta.setUpdateTime(now);
//
//                stepList.add(meta);
//            }
//
//            if (!stepList.isEmpty()) {
//                taskStatusService.saveBatch(stepList);
//            }
//    }
//
//    public void addTaskOutput(Long sceneTaskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Output deepSearchOutput) {
//        if (deepSearchOutput == null) {
//            log.debug("addTaskOutput: deepSearchOutput is null, taskId={}", sceneTaskId);
//            return;
//        }
//        ReentrantLock lock = getLock(sceneTaskId);
//        lock.lock();
//        try {
//            DeepSearchTaskRecordEntity meta = new DeepSearchTaskRecordEntity();
//            meta.setTaskId(sceneTaskId);
//            meta.setSceneId(sceneId);
//            meta.setGoal(goal);
//            meta.setFlowId(flowId);
//            meta.setStepType("Worker");
//            meta.setStatus("RUNNING");
//            meta.setProgress(0);
//
//            meta.setActionName(deepSearchOutput.getName());
//            meta.setActionId(TASK_META_ACTION_ID);
//            meta.setResult(deepSearchOutput.getDescription());
//
//            Date now = new Date();
//            meta.setStartTime(now);
//            meta.setAddTime(now);
//            meta.setUpdateTime(now);
//
//            taskStatusService.save(meta);
//        } catch (Exception e) {
//            log.error("addTaskOutput error, taskId={}", sceneTaskId, e);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    /**
//     * 添加任务输出步骤
//     */
//    @Async
//    public void addTaskOutputStep(Long sceneTaskId, Long sceneId, String goal, String flowId, DeepSearchFlow.Output deepSearchOutput) {
//        if (deepSearchOutput == null || deepSearchOutput.getActions() == null || deepSearchOutput.getActions().isEmpty()) {
//            return;
//        }
//
//        List<DeepSearchTaskRecordEntity> stepList = new ArrayList<>();
//
//        for (DeepSearchFlow.StepAction step : deepSearchOutput.getActions()) {
//            DeepSearchTaskRecordEntity meta = new DeepSearchTaskRecordEntity();
//            meta.setTaskId(sceneTaskId);
//            meta.setSceneId(sceneId);
//            meta.setGoal(goal);
//            meta.setFlowId(flowId);
//            meta.setStepType("STEP");
//            meta.setStatus("COMPLETED");
//            meta.setProgress(0);
//
//            meta.setActionId(step.getActionId());
//            meta.setIcon(step.getIcon());
//            meta.setActionName(step.getActionName());
//            meta.setThink(step.getThink());
//            meta.setResult(step.getResult());
//            meta.setStatus(step.getStatus());
//            meta.setErrorMsg(step.getErrorMsg());
//
//            Date now = new Date();
//            meta.setStartTime(now);
//            meta.setAddTime(now);
//            meta.setUpdateTime(now);
//
//            stepList.add(meta);
//        }
//
//        if (!stepList.isEmpty()) {
//            taskStatusService.saveBatch(stepList);
//        }
//    }
//}