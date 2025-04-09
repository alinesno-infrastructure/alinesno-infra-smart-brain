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
    @Column(name = "rule_level", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "规则级别")
    private String ruleLevel;

    @TableField
    @Column(name = "rule_content", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "规则内容")
    private String ruleContent;

    @TableField
    @Column(name = "contract_type_id", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "所属合同类型")
    private String contractTypeId;

}
