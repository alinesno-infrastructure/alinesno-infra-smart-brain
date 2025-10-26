package com.alinesno.infra.smart.scene.entity;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 深度搜索任务实体类
 * @author luoxiaodong
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("deep_search_task")
public class DeepSearchTaskEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private long sceneId;

    @TableField
    @Column(name = "deepsearch_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "深度检索场景ID")
    private long deepsearchSceneId;

    @TableField("gen_status")
    @Column(type = MySqlTypeConstant.INT, length = 1, comment = "生成状态(1已生成|0未生成|2生成中|3生成失败)")
    private Integer genStatus = 0 ;

    @TableField
    @Column(name = "attachments", type = MySqlTypeConstant.VARCHAR, comment = "附件ID号，以逗号进行分隔")
    private String attachments;

    @TableField
    @Column(name = "search_planner_engineer", type = MySqlTypeConstant.BIGINT, length = 32, comment = "搜索规划者ID")
    private Long searchPlannerEngineer ;

    @TableField
    @Column(name = "prompt_content", type = MySqlTypeConstant.TEXT, comment = "用于存储与数据分析相关的提示内容，可作为引导数据分析流程或操作的文本信息")
    private String promptContent;

    @TableField
    @Column(name = "task_name", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "任务名称")
    private String taskName ;

    @TableField
    @Column(name = "task_description", type = MySqlTypeConstant.TEXT, comment = "任务描述")
    private String taskDescription;

    @TableField
    @Column(name = "task_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "任务状态(not_run未运行|completed已运行|running运行中|generating生成中|cancelling取消中|cancelled已取消|failed运行失败)")
    private String taskStatus;

    @TableField
    @Column(name = "channel_stream_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "频道流ID")
    private String channelStreamId ;

    @TableField
    @Column(name = "task_start_time", type = MySqlTypeConstant.DATETIME, comment = "任务开始时间")
    private Date taskStartTime;

    @TableField
    @Column(name = "task_end_time", type = MySqlTypeConstant.DATETIME, comment = "任务结束时间")
    private Date taskEndTime;

    @TableField
    @Column(name = "task_plan_json", type = MySqlTypeConstant.TEXT, comment = "任务计划JSON")
    private String taskPlanJson ;

    @TableField
    @Column(name = "error_message", type = MySqlTypeConstant.TEXT, comment = "设置错误信息")
    private String errorMessage ;

    public static DeepSearchTaskEntity createDemoTask() {
        DeepSearchTaskEntity task = new DeepSearchTaskEntity();
        task.setTaskName("Demo任务");
        task.setTaskDescription("这是一个Demo任务");
        task.setTaskStatus("not_run");
        task.setTaskStartTime(new Date());
        task.setTaskEndTime(new Date());
        task.setTaskPlanJson("{}");
        task.setErrorMessage("");
        task.setId(IdUtil.getSnowflakeNextId());
        return task;
    }
}
