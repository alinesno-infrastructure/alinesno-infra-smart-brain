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

    // 事件类型枚举
    public enum Type {
        TASK_CREATE,     // 任务创建
        PLAN_CREATE,     // 规划创建
        PLAN_STEP,       // 规划步骤
        WORKER_STEP,     // 工作步骤
        WORKER_STEP_ACTION, // 工作步骤动作
        OUTPUT_CREATE,   // 输出创建
        OUTPUT_STEP,     // 输出步骤
        STATUS_UPDATE,   // 状态更新
        PROGRESS_UPDATE  // 进度更新
    }

    private Long taskId;          // 任务ID
    private Long sceneId;         // 场景ID
    private String flowId;        // 流程ID
    private String goal;          // 任务目标
    private Type eventType;       // 事件类型
    private Integer seq;          // 顺序号（全局自增）
    private DeepSearchFlow.Plan plan; // 规划对象
    private DeepSearchFlow.Step step; // 步骤对象
    private DeepSearchFlow.Output output; // 输出对象
    private String status;        // 状态信息
    private Integer progress;     // 进度值
}