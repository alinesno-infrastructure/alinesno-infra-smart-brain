// DeepSearchTaskEvent.java
package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record;

import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 深度搜索任务事件
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeepSearchTaskEvent implements Serializable {

    public enum Type {
        TASK_CREATE,     // 任务创建
        PLAN_CREATE,     // 规划创建
        PLAN_STEP,       // 规划步骤
        USER_QUESTION,   // 用户问题
        PLAN_MARK,       // 规划步骤标记
        PLAN_STEP_SINGLE, // 规划步骤动作
        PLAN_STEP_SINGLE_MARK, // 规划步骤动作标记
        WORKER_STEP,     // 工作步骤
        WORKER_STEP_ACTION, // 工作步骤动作
        WORKER_STEP_ACTION_SINGLE, // 工作步骤动作
        WORKER_STEP_SINGLE_MARK, // 工作步骤动作标记
        OUTPUT_CREATE,   // 输出创建
        OUTPUT_STEP,     // 输出步骤
        OUTPUT_MARK,     // 输出步骤标记
        OUTPUT_STEP_SINGLE, // 输出步骤动作
        STATUS_UPDATE,   // 状态更新
        WORKER_MARK,     // 工作步骤标记
        PROGRESS_UPDATE, // 进度更新
        ATTACHMENT_CREATE , // 附件创建事件
        USER_ATTACHMENT_CREATE  // 用户提交附件创建事件
    }

    private String planId;         // 规划ID
    private Long taskId;          // 任务ID
    private Long sceneId;         // 场景ID
    private String flowId;        // 流程ID
    private String goal;          // 任务目标
    private Type eventType;       // 事件类型
    private Integer seq;          // 顺序号（全局自增）
    private String sessionId;     // 会话ID（所有事件都需携带）
    private DeepSearchFlow.Plan plan; // 规划对象
    private DeepSearchFlow.Step step; // 步骤对象
    private DeepSearchFlow.StepAction stepAction; // 步骤执行对象
    private DeepSearchFlow.Output output; // 输出对象
    private DeepSearchFlow.FileAttachmentDto attachment; // 附件对象
    private String status;        // 状态信息
    private Integer progress;     // 进度值
    private Long timestamp;       // 时间戳
}