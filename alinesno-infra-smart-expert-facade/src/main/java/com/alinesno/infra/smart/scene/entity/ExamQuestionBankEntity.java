package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题库管理表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("exam_question_bank")
@Table(name = "exam_question_bank")
public class ExamQuestionBankEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32 , comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "bank_name", type = MySqlTypeConstant.VARCHAR, length = 255, isNull = false, comment = "题库名称")
    private String bankName;

    @TableField
    @Column(name = "description", type = MySqlTypeConstant.TEXT, comment = "题库描述")
    private String description;

    @TableField
    @Column(name = "subject", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "所属学科/科目")
    private String subject;

    @TableField
    @Column(name = "status", type = MySqlTypeConstant.TINYINT, defaultValue = "1", comment = "状态：0-禁用，1-启用")
    private Integer status;

    @TableField
    @Column(name = "is_public", type = MySqlTypeConstant.TINYINT, defaultValue = "0", comment = "是否公开：0-私有，1-公开")
    private Integer isPublic;

}