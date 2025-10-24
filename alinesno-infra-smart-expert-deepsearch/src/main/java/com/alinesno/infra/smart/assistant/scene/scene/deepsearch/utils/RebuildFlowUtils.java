package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;

import com.alibaba.fastjson2.JSON;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskRecordEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DeepSearchFlowUtils.java
 */
public class RebuildFlowUtils {
    /**
     * 从记录重建 DeepSearchFlow
     */
    public static DeepSearchFlow rebuildDeepSearchFlowFromRecords(List<DeepSearchTaskRecordEntity> records, DeepSearchTaskEntity task) {
        DeepSearchFlow flow = new DeepSearchFlow(task.getChannelStreamId());
        flow.setSceneId(task.getSceneId());
        flow.setTaskId(task.getId());

        // 按类型分组记录
        Map<String, List<DeepSearchTaskRecordEntity>> recordsByType = records.stream()
                .collect(Collectors.groupingBy(DeepSearchTaskRecordEntity::getStepType));

        // 重建 Plan
        rebuildPlanFromRecords(flow, recordsByType.get("PLAN"), recordsByType.get("PLAN_STEP"));

        // 重建 Steps
        rebuildStepsFromRecords(flow, recordsByType.get("WORKER"), recordsByType.get("WORKER_ACTION"));

        // 重建 Output
        rebuildOutputFromRecords(flow, recordsByType.get("OUTPUT"), recordsByType.get("OUTPUT_STEP"),recordsByType.get("ATTACHMENT"));

        return flow;
    }

    public static List<DeepSearchFlow> rebuildDeepSearchFlowsBySession(List<DeepSearchTaskRecordEntity> records, DeepSearchTaskEntity task) {
        // 1. 按 sessionId 分组（无 sessionId 的归为默认组）
        Map<String, List<DeepSearchTaskRecordEntity>> recordsBySession = records.stream()
                .collect(Collectors.groupingBy(record ->
                        StringUtils.isEmpty(record.getSessionId()) ? "DEFAULT" : record.getSessionId()
                ));

        List<DeepSearchFlow> flowList = new ArrayList<>();
        for (Map.Entry<String, List<DeepSearchTaskRecordEntity>> entry : recordsBySession.entrySet()) {
            String sessionId = entry.getKey();
            List<DeepSearchTaskRecordEntity> sessionRecords = entry.getValue();

            // 2. 为每个 session 重建一个 DeepSearchFlow
            DeepSearchFlow flow = new DeepSearchFlow(task.getChannelStreamId());
            flow.setSceneId(task.getSceneId());
            flow.setTaskId(task.getId());
            flow.setSessionId(sessionId); // 给 flow 绑定 sessionId

            // 3. 提取当前 session 的用户问题（StepType = USER_QUESTION）
            List<DeepSearchTaskRecordEntity> userQuestionRecords = sessionRecords.stream()
                    .filter(r -> "USER_QUESTION".equals(r.getStepType()))
                    .toList();
            if (!userQuestionRecords.isEmpty()) {
                flow.setUserQuestion(userQuestionRecords.get(0).getResult()); // 存储用户问题
            }

            // 4. 提取当前 session 的附件（StepType = USER_ATTACHMENT）
            List<DeepSearchTaskRecordEntity> attachmentRecords = sessionRecords.stream()
                    .filter(r -> "USER_ATTACHMENT".equals(r.getStepType()))
                    .toList();
            if (!attachmentRecords.isEmpty()) {
                List<DeepSearchFlow.FileAttachmentDto> attachments = attachmentRecords.stream()
                        .map(RebuildFlowUtils::convertRecordToAttachment)
                        .collect(Collectors.toList());
                flow.setUserAttachments(attachments); // 存储用户附件
            }

            // 5. 提取当前 session 的附件（StepType = ATTACHMENT）
            List<DeepSearchTaskRecordEntity> outputAttachmentRecords = sessionRecords.stream()
                    .filter(r -> "ATTACHMENT".equals(r.getStepType()))
                    .toList();

            // 5. 重建当前 session 的 Plan/Steps/Output（复用原有逻辑）
            Map<String, List<DeepSearchTaskRecordEntity>> recordsByType = sessionRecords.stream()
                    .collect(Collectors.groupingBy(DeepSearchTaskRecordEntity::getStepType));
            rebuildPlanFromRecords(flow, recordsByType.get("PLAN"), recordsByType.get("PLAN_STEP"));
            rebuildStepsFromRecords(flow, recordsByType.get("WORKER"), recordsByType.get("WORKER_ACTION"));
            rebuildOutputFromRecords(flow, recordsByType.get("OUTPUT"), recordsByType.get("OUTPUT_STEP"), outputAttachmentRecords);

            flowList.add(flow);
        }

        // 6. 按 seq 排序（确保轮次顺序正确）
        return flowList.stream()
                .sorted(Comparator.comparingInt(flow -> {
                    // 取当前 flow 第一条记录的 seq 作为排序依据
                    List<DeepSearchTaskRecordEntity> sessionRecords = recordsBySession.get(flow.getSessionId());
                    return sessionRecords != null && !sessionRecords.isEmpty()
                            ? sessionRecords.get(0).getSeq()
                            : 0;
                }))
                .collect(Collectors.toList());
    }

    public static  void rebuildPlanFromRecords(DeepSearchFlow flow, List<DeepSearchTaskRecordEntity> planRecords,
                                        List<DeepSearchTaskRecordEntity> planStepRecords) {
        if (planRecords != null && !planRecords.isEmpty()) {
            DeepSearchTaskRecordEntity planRecord = planRecords.get(0);
            DeepSearchFlow.Plan plan = new DeepSearchFlow.Plan(planRecord.getActionName());
            plan.setId(planRecord.getPlanId());
            plan.setDescription(planRecord.getResult());
            plan.setStatus(planRecord.getStatus());

            // 重建 Plan Actions
            if (planStepRecords != null) {
                List<DeepSearchFlow.StepAction> planActions = planStepRecords.stream()
                        .map(RebuildFlowUtils::convertRecordToStepAction)
                        .collect(Collectors.toList());
                plan.setActions(planActions);
            }

            flow.setPlan(plan);
        }
    }

    public static  void rebuildStepsFromRecords(DeepSearchFlow flow, List<DeepSearchTaskRecordEntity> workerRecords,
                                         List<DeepSearchTaskRecordEntity> workerActionRecords) {
        if (workerRecords != null) {
            List<DeepSearchFlow.Step> steps = new ArrayList<>();

            for (DeepSearchTaskRecordEntity workerRecord : workerRecords) {
                DeepSearchFlow.Step step = new DeepSearchFlow.Step();
                step.setId(workerRecord.getStepId());
                step.setName(workerRecord.getActionName());
                step.setDescription(workerRecord.getResult());
                step.setStatus(workerRecord.getStatus());

                // 重建 Step Actions
                if (workerActionRecords != null) {
                    List<DeepSearchFlow.StepAction> stepActions = workerActionRecords.stream()
                            .filter(action -> workerRecord.getStepId().equals(action.getStepId()))
                            .map(RebuildFlowUtils::convertRecordToStepAction)
                            .collect(Collectors.toList());
                    step.setActions(stepActions);
                }

                steps.add(step);
            }

            flow.setSteps(steps);
        }
    }

    public static  void rebuildOutputFromRecords(DeepSearchFlow flow,
                                          List<DeepSearchTaskRecordEntity> outputRecords,
                                          List<DeepSearchTaskRecordEntity> outputStepRecords ,
                                          List<DeepSearchTaskRecordEntity> attachmentRecords) {

        if (outputRecords != null && !outputRecords.isEmpty()) {
            DeepSearchTaskRecordEntity outputRecord = outputRecords.get(0);
            DeepSearchFlow.Output output = new DeepSearchFlow.Output();
            output.setId(outputRecord.getOutputId());
            output.setName(outputRecord.getActionName());
            output.setDescription(outputRecord.getResult());
            output.setStatus(outputRecord.getStatus());

            // 重建 Output Actions
            if (outputStepRecords != null) {

                // 设置总结的result字段为空

                List<DeepSearchFlow.StepAction> outputActions = outputStepRecords.stream()
                        .map(RebuildFlowUtils::convertRecordToStepAction)
                        .peek(action -> action.setResult(StringUtils.EMPTY))
                        .collect(Collectors.toList());

                output.setActions(outputActions);
            }

            // ✅ 重建 Output Attachments
            if (attachmentRecords != null) {
                List<DeepSearchFlow.FileAttachmentDto> attachments = attachmentRecords.stream()
                        .map(RebuildFlowUtils::convertRecordToAttachment)
                        .collect(Collectors.toList());
                output.setAttachments(attachments);
            }

            flow.setOutput(output);
        }
    }

    public static  DeepSearchFlow.StepAction convertRecordToStepAction(DeepSearchTaskRecordEntity record) {
        DeepSearchFlow.StepAction action = new DeepSearchFlow.StepAction();
        action.setActionId(record.getActionId());
        action.setActionType(record.getActionType());
        action.setActionName(record.getActionName());
        action.setThink(record.getThink());
        action.setResult(record.getResult());
        action.setStatus(record.getStatus());
        action.setIcon(record.getIcon());
        return action;
    }

    /**
     * 反序列化数据库记录为附件 FileAttachmentDto
     */
    public static DeepSearchFlow.FileAttachmentDto convertRecordToAttachment(DeepSearchTaskRecordEntity record) {
        try {
            // 附件记录的 result 存储了完整 JSON
            return JSON.parseObject(record.getResult(), DeepSearchFlow.FileAttachmentDto.class);
        } catch (Exception e) {
            // 如果反序列化失败，做兜底处理
            DeepSearchFlow.FileAttachmentDto dto = new DeepSearchFlow.FileAttachmentDto();
            dto.setId(record.getStepId());
            dto.setName(record.getActionName());
            dto.setDesc(record.getGoal());
            dto.setType("unknown");
            return dto;
        }
    }
}
