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

/**
 * 考试试卷实体类对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("exam_pager")
@Table(name = "exam_pager")
public class ExamPagerEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "title", type = MySqlTypeConstant.VARCHAR, length = 255, isNull = false, comment = "试卷标题")
    private String title;

    // 难度Difficulty
    @TableField
    @Column(name = "difficulty", type = MySqlTypeConstant.VARCHAR, length = 10, comment = "难度：easy-简单，medium-普通，hard-困难")
    private String difficulty;

    @TableField
    @Column(name = "description", type = MySqlTypeConstant.TEXT, comment = "试卷描述")
    private String description;

    @TableField
    @Column(name = "status", type = MySqlTypeConstant.TINYINT, defaultValue = "0", comment = "状态：0-草稿，1-发布，2-归档")
    private Integer status;

    @TableField
    @Column(name = "ai_generated", type = MySqlTypeConstant.TINYINT, defaultValue = "0", comment = "是否AI生成：0-否，1-是")
    private Integer aiGenerated;

    // 显示设置
    @TableField
    @Column(name = "show_outline", type = MySqlTypeConstant.TINYINT, defaultValue = "1", comment = "是否显示大纲：0-否，1-是")
    private Integer showOutline;

    @TableField
    @Column(name = "show_question_number", type = MySqlTypeConstant.TINYINT, defaultValue = "1", comment = "是否显示题目编号：0-否，1-是")
    private Integer showQuestionNumber;

    @TableField
    @Column(name = "languages", type = MySqlTypeConstant.VARCHAR, length = 100, defaultValue = "zh-CN", comment = "支持的语言列表，逗号分隔")
    private String languages;

    // 填答设置
    @TableField
    @Column(name = "start_time", type = MySqlTypeConstant.DATETIME, comment = "开始时间")
    private Date startTime;

    @TableField
    @Column(name = "end_time", type = MySqlTypeConstant.DATETIME, comment = "结束时间")
    private Date endTime;

    @TableField
    @Column(name = "duration", type = MySqlTypeConstant.INT, comment = "考试时长（分钟）")
    private Integer duration;

    @TableField
    @Column(name = "auto_submit", type = MySqlTypeConstant.TINYINT, defaultValue = "1", comment = "时间到是否自动提交：0-否，1-是")
    private Integer autoSubmit;

    @TableField
    @Column(name = "respondent_type", type = MySqlTypeConstant.TINYINT, defaultValue = "0", comment = "答题人类型：0-手机号，1-姓名+手机号，2-姓名+学号")
    private Integer respondentType;

    @TableField
    @Column(name = "allow_anonymous", type = MySqlTypeConstant.TINYINT, defaultValue = "0", comment = "是否允许匿名答题：0-否，1-是")
    private Integer allowAnonymous;

    @TableField
    @Column(name = "account_type", type = MySqlTypeConstant.VARCHAR, length = 50, defaultValue = "phone", comment = "填答账号类型，逗号分隔")
    private String accountType;

    @TableField
    @Column(name = "submit_limit_per_account", type = MySqlTypeConstant.INT, defaultValue = "1", comment = "每个账号允许的答题次数")
    private Integer submitLimitPerAccount;

    @TableField
    @Column(name = "submit_limit_per_ip", type = MySqlTypeConstant.INT, defaultValue = "1", comment = "每个IP允许的答题次数")
    private Integer submitLimitPerIp;

    // 答后设置
    @TableField
    @Column(name = "allow_modify", type = MySqlTypeConstant.TINYINT, defaultValue = "0", comment = "是否允许修改已填内容：0-否，1-是")
    private Integer allowModify;

    @TableField
    @Column(name = "daily_check_in", type = MySqlTypeConstant.TINYINT, defaultValue = "0", comment = "是否需要每日打卡：0-否，1-是")
    private Integer dailyCheckIn;

    // 导出设置
    @TableField
    @Column(name = "export_format", type = MySqlTypeConstant.VARCHAR, length = 20, defaultValue = "pdf", comment = "导出格式，逗号分隔")
    private String exportFormat;

    @TableField
    @Column(name = "show_score", type = MySqlTypeConstant.TINYINT, defaultValue = "1", comment = "导出时是否显示分数：0-否，1-是")
    private Integer showScore;

    @TableField
    @Column(name = "show_answer", type = MySqlTypeConstant.TINYINT, defaultValue = "0", comment = "导出时是否显示答案：0-否，1-是")
    private Integer showAnswer;
}