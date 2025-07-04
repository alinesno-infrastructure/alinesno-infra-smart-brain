package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试题目，类型包括：单选题、多选题、判断题、填空题、简答题、编程题
 */
@EqualsAndHashCode(callSuper = true)
@TableName("exam_question")
@Data
public class ExamQuestionEntity extends InfraBaseEntity {

    @TableField("scene_id")
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private Long sceneId; // 关联的场景ID

    @TableField("assessment_content")
    @Column(name = "assessment_content", type = MySqlTypeConstant.TEXT, comment = "考核内容")
    private String assessmentContent;

    @TableField("is_required")
    @Column(name = "is_required", type = MySqlTypeConstant.TINYINT, defaultValue = "1", comment = "是否必填：0-否，1-是")
    private Boolean isRequired;

    @TableField("score")
    @Column(name = "score", type = MySqlTypeConstant.INT, defaultValue = "0", comment = "题目分数")
    private Integer score;

    @TableField("question")
    @Column(name = "question", type = MySqlTypeConstant.TEXT, comment = "问题内容")
    private String question;

    @TableField("answer_analysis")
    @Column(name = "answer_analysis", type = MySqlTypeConstant.TEXT, comment = "答案解析")
    private String answerAnalysis;

    @TableField("type")
    @Column(name = "type", type = MySqlTypeConstant.VARCHAR, length = 50, comment = "问题类型")
    private String type;

    @TableField("sort_order")
    @Column(name = "sort_order", type = MySqlTypeConstant.INT, defaultValue = "1", comment = "排序字段")
    private Integer sortOrder;

    @TableField("blanks")
    @Column(name = "blanks", type = MySqlTypeConstant.TEXT, comment = "填空题空白位置信息")
    private String blanks;

    @TableField("answers")
    @Column(name = "answers", type = MySqlTypeConstant.TEXT, comment = "答案选项信息")
    private String answers;

    @TableField("correct_answer")
    @Column(name = "correct_answer", type = MySqlTypeConstant.TEXT, comment = "答案选项信息")
    private String correctAnswer;

    @TableField("bank_id")
    @Column(name = "bank_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "所属题库ID")
    private Long bankId;

    @TableField("pager_id")
    @Column(name = "pager_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "所属试卷ID")
    private Long pagerId;

    @TableField("difficulty")
    @Column(name = "difficulty", type = MySqlTypeConstant.TINYINT, comment = "难度等级：1-简单，2-中等，3-困难")
    private Integer difficulty;

    @TableField("status")
    @Column(name = "status", type = MySqlTypeConstant.TINYINT, defaultValue = "1", comment = "状态：0-禁用，1-启用")
    private Integer status;
}