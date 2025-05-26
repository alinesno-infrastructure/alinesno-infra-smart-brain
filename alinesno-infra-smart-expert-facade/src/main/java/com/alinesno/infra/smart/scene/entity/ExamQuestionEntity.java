package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 考试题目，类型包括：单选题、多选题、判断题、填空题、简答题、编程题
 */
@EqualsAndHashCode(callSuper = true)
@TableName("exam_question")
@Data
public class ExamQuestionEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private Long sceneId; // 关联的场景ID

    @TableField
    @Column(name = "assessment_content", type = MySqlTypeConstant.TEXT, isNull = false, comment = "考核内容")
    private String assessmentContent;

    @TableField
    @Column(name = "is_required", type = MySqlTypeConstant.TINYINT, isNull = false, defaultValue = "1", comment = "是否必填：0-否，1-是")
    private Boolean isRequired;

    @TableField
    @Column(name = "score", type = MySqlTypeConstant.INT, isNull = false, defaultValue = "0", comment = "题目分数")
    private Integer score;

    @TableField
    @Column(name = "question", type = MySqlTypeConstant.TEXT, isNull = false, comment = "问题内容")
    private String question;

    @TableField
    @Column(name = "answer_analysis", type = MySqlTypeConstant.TEXT, isNull = false, comment = "答案解析")
    private String answerAnalysis;

    @TableField
    @Column(name = "type", type = MySqlTypeConstant.VARCHAR, length = 50, isNull = false, comment = "问题类型")
    private String type;

    @TableField
    @Column(name = "sort_order", type = MySqlTypeConstant.INT, isNull = false, defaultValue = "1", comment = "排序字段")
    private Integer order;

    @TableField
    @Column(name = "blanks", type = MySqlTypeConstant.TEXT, comment = "填空题空白位置信息")
    private String blanks;

    @TableField
    @Column(name = "answers", type = MySqlTypeConstant.TEXT, comment = "答案选项信息")
    private String answers;

    @TableField
    @Column(name = "bank_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "所属题库ID")
    private Long bankId;

    @TableField
    @Column(name = "pager_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "所属试卷ID")
    private Long pagerId ;

    @TableField
    @Column(name = "difficulty", type = MySqlTypeConstant.TINYINT, comment = "难度等级：1-简单，2-中等，3-困难")
    private Integer difficulty;

    @TableField
    @Column(name = "status", type = MySqlTypeConstant.TINYINT, defaultValue = "1", comment = "状态：0-禁用，1-启用")
    private Integer status;

}