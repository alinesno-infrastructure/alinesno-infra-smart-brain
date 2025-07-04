package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 考试信息实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("exam_info")
public class ExamInfoEntity extends InfraBaseEntity {

    @TableField("pager_id")
    @Column(type = MySqlTypeConstant.BIGINT, comment = "试卷Id")
    private Long pagerId;

    @TableField("scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, comment = "场景Id")
    private Long sceneId;
    
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "考试名称")
    @TableField("exam_name")
    private String examName;
    
    @TableField("exam_code")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "考试编号")
    private String examCode;

    @TableField("start_time")
    @Column(type = MySqlTypeConstant.TIMESTAMP, comment = "考试开始时间")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    @Column(type = MySqlTypeConstant.TIMESTAMP, comment = "考试结束时间")
    private LocalDateTime endTime;
    
    @TableField("duration")
    @Column(type = MySqlTypeConstant.INT, comment = "考试时长")
    private Integer duration;
    
    @TableField("exam_type")
    @Column(type = MySqlTypeConstant.INT, comment = "考试类型(1:笔试 2:机考 3:面试)")
    private Integer examType = 2;
    
    @TableField("total_score")
    @Column(type = MySqlTypeConstant.INT, comment = "考试总分")
    private Integer totalScore = 100;
    
    @TableField("exam_pass_score")
    @Column(type = MySqlTypeConstant.INT, comment = "考试通过分数")
    private Integer examPassScore = 60;
    
    @TableField("exam_status")
    @Column(type = MySqlTypeConstant.INT, comment = "考试状态(0:未开始 1:进行中 2:已结束 3:已取消)")
    private Integer examStatus = 0;
    
    @TableField("exam_description")
    @Column(type = MySqlTypeConstant.TEXT, comment = "考试描述")
    private String examDescription;
    
    @Version
    @TableField("exam_version")
    @Column(type = MySqlTypeConstant.INT, comment = "考试版本号(乐观锁)")
    private Integer examVersion = 1;

    // 试卷唯一码
    @TableField("exam_unique_code")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, comment = "试卷唯一码")
    private String examUniqueCode;

}