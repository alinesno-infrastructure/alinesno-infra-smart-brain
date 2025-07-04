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
 * 考试成绩实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("exam_score")
public class ExamScoreEntity extends InfraBaseEntity {

    @TableField("pager_id")
    @Column(type = MySqlTypeConstant.BIGINT, comment = "试卷Id")
    private Long pagerId;

    @TableField("scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, comment = "场景Id")
    private Long sceneId;

    @Column(type = MySqlTypeConstant.BIGINT, comment = "考试ID(关联t_exam_info表)")
    @TableField("exam_info_id")
    private Long examInfoId;
    
    @Column(type = MySqlTypeConstant.BIGINT, comment = "考生ID(关联用户表)")
    @TableField("user_id")
    private Long userId;
    
    @Column(type = MySqlTypeConstant.VARCHAR, length = 50, comment = "考生姓名")
    @TableField("user_name")
    private String userName;
    
    @Column(type = MySqlTypeConstant.VARCHAR, length = 50, comment = "考生学号/工号")
    @TableField("user_code")
    private String userCode;
    
    @Column(type = MySqlTypeConstant.INT, comment = "考试成绩")
    @TableField("score")
    private Integer score;
    
    @Column(type = MySqlTypeConstant.TINYINT, comment = "是否及格(0:不及格 1:及格)")
    @TableField("is_pass")
    private Integer isPass;
    
    @Column(type = MySqlTypeConstant.INT, comment = "考试用时(分钟)")
    @TableField("used_time")
    private Integer usedTime;
    
    @Column(type = MySqlTypeConstant.DATETIME, comment = "提交时间")
    @TableField("submit_time")
    private LocalDateTime submitTime;
    
    @Column(type = MySqlTypeConstant.BIGINT, comment = "阅卷人ID")
    @TableField("reviewer_id")
    private Long reviewerId;
    
    @Column(type = MySqlTypeConstant.VARCHAR, length = 50, comment = "阅卷人姓名")
    @TableField("reviewer_name")
    private String reviewerName;
    
    @Column(type = MySqlTypeConstant.DATETIME, comment = "阅卷时间")
    @TableField("review_time")
    private LocalDateTime reviewTime;
    
    @Column(type = MySqlTypeConstant.VARCHAR, length = 500, comment = "成绩备注")
    @TableField("remark")
    private String remark;

    @Column(type = MySqlTypeConstant.TINYINT, comment = "逻辑删除标志(0:未删除 1:已删除)")
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
    
    @Column(type = MySqlTypeConstant.INT, comment = "版本号(乐观锁)")
    @Version
    @TableField("version")
    private Integer version;

    @TableField("answers")
    @Column(type = MySqlTypeConstant.TEXT , comment = "考生答案")
    private String answers; ;

    @TableField("questions")
    @Column(type = MySqlTypeConstant.TEXT , comment = "试题列表(用于现场留痕)")
    private String questions ;

    // 分析结果保存
    @TableField("analysis_result")
    @Column(type = MySqlTypeConstant.TEXT , comment = "分析结果")
    private String analysisResult ;

}