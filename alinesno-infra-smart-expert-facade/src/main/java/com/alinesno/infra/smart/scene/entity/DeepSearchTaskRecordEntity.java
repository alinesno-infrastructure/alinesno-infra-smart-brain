package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 深度搜索任务/步骤记录实体（合并版）
 * 表名：deep_search_task_status
 *
 * 用法说明：
 *  - step_type = "TASK" : 该行为任务元记录（保存 goal/status/progress/start/end/account_id 等）
 *  - step_type = "PLAN"/"STEP"/"OUTPUT" : 每条为一个 StepAction 的记录（action_id 不为空）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("deep_search_task_record")
public class DeepSearchTaskRecordEntity extends InfraBaseEntity {

    // ---------- 关联字段 ----------
    @TableField
    @Column(name = "task_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "任务ID（sceneTaskId）")
    private Long taskId;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "flow_id", type = MySqlTypeConstant.VARCHAR, length = 64, comment = "流程ID（DeepSearchFlow.flowId）")
    private String flowId;

    // ---------- 区分 TASK 元记录 与 每步记录 ----------
    @TableField
    @Column(name = "step_type", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "记录类型：TASK/PLAN/STEP/OUTPUT")
    private String stepType; // "TASK" or "PLAN"/"STEP"/"OUTPUT"

    @TableField
    @Column(name = "plan_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "计划ID（仅 PLAN/STEP/OUTPUT 行使用）")
    private String planId ;

    @TableField
    @Column(name = "output_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "输出ID（仅 OUTPUT 行使用）")
    private String outputId ;

    // 对于 STEP 类型记录用于关联 step（step.id）
    @TableField
    @Column(name = "step_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "步骤ID（仅 STEP 类型有值）")
    private String stepId;

    // 每个动作唯一 id（StepAction.actionId）；TASK 元记录使用固定值 "TASK_META"
    @TableField
    @Column(name = "action_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "动作ID（StepAction.actionId 或 TASK_META）")
    private String actionId;

    @TableField
    @Column(name = "icon", type = MySqlTypeConstant.VARCHAR, length = 64, comment = "图标")
    private String icon ;

    @TableField
    @Column(name = "action_type", type = MySqlTypeConstant.VARCHAR, length = 64, comment = "动作类型（TOOL/ANALYSIS/...）")
    private String actionType;

    @TableField
    @Column(name = "action_name", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "动作名称/描述")
    private String actionName;

    // think/result 可作为流式文本追加或覆盖
    @TableField
    @Column(name = "think", type = MySqlTypeConstant.TEXT, comment = "思考内容（中间推理/流式内容，可追加）")
    private String think;

    @TableField
    @Column(name = "result", type = MySqlTypeConstant.LONGTEXT, comment = "结果内容（最终输出或中间输出，可追加）")
    private String result;

    @TableField
    @Column(name = "status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "状态：TASK 行为 RUNNING/COMPLETED/FAILED，动作行为 UNDO/DOING/DONE/FAILED")
    private String status;

    @TableField
    @Column(name = "error_msg", type = MySqlTypeConstant.VARCHAR, length = 512, comment = "错误信息")
    private String errorMsg ; // 错误信息

    @TableField
    @Column(name = "seq", type = MySqlTypeConstant.INT, length = 11, comment = "排序序号（用于前端按顺序重建流程）")
    private Integer seq;

    @TableField
    @Column(name = "extra", type = MySqlTypeConstant.TEXT, comment = "扩展字段（JSON 字符串，可存 error、metadata 等）")
    private String extra;

    @TableField
    @Column(name = "storage_id", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "大文本/文件存储ID（如使用对象存储）")
    private String storageId;

    // 任务级元数据（仅 TASK 行有意义）
    @TableField
    @Column(name = "goal", type = MySqlTypeConstant.VARCHAR, length = 1024, comment = "搜索目标（仅 TASK 行有意义）")
    private String goal;

    @TableField
    @Column(name = "progress", type = MySqlTypeConstant.INT, length = 3, comment = "进度百分比(0-100)，仅 TASK 行使用")
    private Integer progress = 0;

    @TableField
    @Column(name = "start_time", type = MySqlTypeConstant.DATETIME, comment = "开始时间（TASK 行使用）")
    private Date startTime;

    @TableField
    @Column(name = "end_time", type = MySqlTypeConstant.DATETIME, comment = "结束时间（TASK 行使用）")
    private Date endTime;

    @TableField
    @Column(name = "timestamp", type = MySqlTypeConstant.BIGINT, comment = "时间戳")
    private Long timestamp;

}