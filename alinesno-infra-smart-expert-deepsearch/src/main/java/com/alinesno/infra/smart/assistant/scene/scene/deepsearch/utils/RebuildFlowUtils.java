package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;

import com.alibaba.fastjson2.JSON;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskRecordEntity;

import java.util.ArrayList;
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
