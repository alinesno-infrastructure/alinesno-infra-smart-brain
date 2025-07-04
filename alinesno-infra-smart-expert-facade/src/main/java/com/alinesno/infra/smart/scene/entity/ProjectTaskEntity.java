package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_task")
@Table(comment = "项目检索任务")
public class ProjectTaskEntity extends InfraBaseEntity {

    @TableField("scene_id")
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 20, comment = "场景id")
    private Long sceneId;

    @TableField("project_research_scene_id")
    @Column(name = "project_research_scene_id", type = MySqlTypeConstant.BIGINT, length = 20, comment = "项目跟进场景id")
    private Long projectResearchSceneId;

    @TableField("task_name")
    @Column(name = "task_name", type = MySqlTypeConstant.TEXT, comment = "任务名称")
    private String taskName;

    @TableField
    @Column(name = "task_description", type = MySqlTypeConstant.LONGTEXT, comment = "任务描述")
    private String taskDescription;

    @TableField
    @Column(name = "task_status", type = MySqlTypeConstant.INT, length = 2, comment = "任务状态(0未运行|1已运行|2运行中|9运行失败)")
    private Integer taskStatus;

    @TableField
    @Column(name = "task_start_time", type = MySqlTypeConstant.DATETIME, comment = "任务开始时间")
    private Date taskStartTime;

    @TableField
    @Column(name = "task_end_time", type = MySqlTypeConstant.DATETIME, comment = "任务结束时间")
    private Date taskEndTime;

    @TableField
    @Column(name = "attachments", type = MySqlTypeConstant.VARCHAR, comment = "附件ID号，以逗号进行分隔")
    private String attachments;

    @TableField
    @Column(name = "dataset_group_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "数据集组ID")
    private Long datasetGroupId ;

    @TableField
    @Column(name = "outline", type = MySqlTypeConstant.TEXT, comment = "任务大纲")
    private String outline ;

    @TableField("channel_stream_id")
    @Column(name = "channel_stream_id", type = MySqlTypeConstant.VARCHAR, length = 64, comment = "运行时的流信息ID")
    private String channelStreamId;

    @TableField("failure_reason")
    @Column(name = "failure_reason", type = MySqlTypeConstant.TEXT, comment = "任务失败原因")
    private String failureReason;

    @TableField("detailed_content")
    @Column(name = "detailed_content", type = MySqlTypeConstant.LONGTEXT, comment = "详细执行内容")
    private String detailedContent;

    @TableField("summarized_content")
    @Column(name = "summarized_content", type = MySqlTypeConstant.LONGTEXT, comment = "总结内容")
    private String summarizedContent;
}