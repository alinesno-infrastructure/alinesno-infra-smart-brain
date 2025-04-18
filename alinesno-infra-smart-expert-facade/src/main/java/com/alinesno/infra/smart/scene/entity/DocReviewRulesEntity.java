package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档审核数据集，用于审核的规则库(审查清单)
 */
@EqualsAndHashCode(callSuper = true)
@TableName("document_review_rules")
@Data
public class DocReviewRulesEntity extends InfraBaseEntity {

    // 规则名称、规则级别、规则内容、所属合同类型
    @TableField
    @Column(name = "rule_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "规则名称")
    private String ruleName;

    @TableField
    @Column(name = "risk_level", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "风险级别")
    private String riskLevel;

    @TableField
    @Column(name = "rule_content", type = MySqlTypeConstant.TEXT, comment = "规则内容")
    private String ruleContent;

    // 审核立场
    @TableField
    @Column(name = "review_position", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "审核立场")
    private String reviewPosition;

    // 所属分组
    @TableField
    @Column(name = "group_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "所属分组")
    private Long groupId;

}
