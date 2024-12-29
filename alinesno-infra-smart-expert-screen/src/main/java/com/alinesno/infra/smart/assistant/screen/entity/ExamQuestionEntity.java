package com.alinesno.infra.smart.assistant.screen.entity;

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
    @Column(name = "screen_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private Long screenId; // 关联的场景ID

    @TableField
    @Column(name = "question_type", type = MySqlTypeConstant.VARCHAR, length = 20, comment = "题目类型")
    private String questionType; // 题目类型

    @TableField
    @Column(name = "content", type = MySqlTypeConstant.TEXT, comment = "题目内容")
    private String content; // 题目内容

    @TableField
    @Column(name = "options", type = MySqlTypeConstant.JSON, comment = "选项列表，适用于选择题")
    private List<String> options; // 选项列表，适用于选择题

    @TableField
    @Column(name = "correct_answer", type = MySqlTypeConstant.TEXT, comment = "正确答案")
    private String correctAnswer; // 正确答案

    @TableField
    @Column(name = "difficulty_level", type = MySqlTypeConstant.VARCHAR, length = 10, comment = "难度等级")
    private String difficultyLevel; // 题目难度等级

    @TableField
    @Column(name = "category", type = MySqlTypeConstant.VARCHAR, length = 50, comment = "题目分类")
    private String category; // 题目分类

    @TableField
    @Column(name = "score", type = MySqlTypeConstant.INT, comment = "分值")
    private Integer score; // 分值

    @TableField
    @Column(name = "user_answer", type = MySqlTypeConstant.TEXT, comment = "用户回答")
    private String userAnswer; // 用户的回答

    // 根据用户答题情况，分析出用户答题的问题，还有提供建议
    @TableField
    @Column(name = "analysis", type = MySqlTypeConstant.TEXT, comment = "分析结果")
    private String analysis;

}