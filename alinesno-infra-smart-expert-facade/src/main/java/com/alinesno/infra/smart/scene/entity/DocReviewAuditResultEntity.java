package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档审核结果实体类
 */
@EqualsAndHashCode(callSuper = true)
@TableName("document_review_audit_result")
@Data
public class DocReviewAuditResultEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "audit_id", type = MySqlTypeConstant.BIGINT , length = 32, comment = "审核清单ID")
    private Long auditId;

    @TableField
    @Column(name = "rule_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "规则ID")
    private Long ruleId;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "task_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "任务ID")
    private Long taskId ;

    @TableField
    @Column(name = "doc_review_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "文档审核场景ID")
    private Long docReviewSceneId ;

    @TableField
    @Column(name = "audit_result", type = MySqlTypeConstant.VARCHAR, length = 1024, comment = "审核结果")
    private String auditResult;

    @TableField
    @Column(name = "audit_opinion", type = MySqlTypeConstant.VARCHAR, length = 1024, comment = "审核意见")
    private String auditOpinion;

    @TableField
    @Column(name = "audit_status", type = MySqlTypeConstant.INT, length = 2, comment = "审核状态")
    private int auditStatus;

    @TableField
    @Column(name = "modification_reason", type = MySqlTypeConstant.TEXT, comment = "修改原因")
    private String modificationReason;

    @TableField
    @Column(name = "original_content", type = MySqlTypeConstant.TEXT, comment = "原内容")
    private String originalContent;

    @TableField
    @Column(name = "risk_level", type = MySqlTypeConstant.VARCHAR, length = 10, comment = "风险等级")
    private String riskLevel;

    @TableField
    @Column(name = "suggested_content", type = MySqlTypeConstant.TEXT, comment = "建议修改的内容")
    private String suggestedContent;

}
