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
 * 文档审核任务
 */
@EqualsAndHashCode(callSuper = true)
@TableName("video_generation_task")
@Data
public class VideoGenTaskEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "task_name", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "任务名称")
    private String taskName;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "document_review_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "文档审核场景ID")
    private Long documentReviewSceneId;

    @TableField
    @Column(name = "task_description", type = MySqlTypeConstant.LONGTEXT, comment = "任务描述")
    private String taskDescription;

    @TableField
    @Column(name = "task_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "任务状态")
    private String taskStatus;

    @TableField
    @Column(name = "task_start_time", type = MySqlTypeConstant.DATETIME, comment = "任务开始时间")
    private Date taskStartTime;

    @TableField
    @Column(name = "task_end_time", type = MySqlTypeConstant.DATETIME, comment = "任务结束时间")
    private Date taskEndTime;


}