package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档审核规则配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("content_formatter_rule")
public class ContentFormatterRuleEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "rule_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "规则名称")
    private String ruleName;

    @TableField
    @Column(name = "risk_level", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "风险级别")
    private String riskLevel;

    @TableField
    @Column(name = "rule_content", type = MySqlTypeConstant.TEXT, comment = "规则内容")
    private String ruleContent;

    @TableField
    @Column(comment = "排版分组" , length = 50 , type = MySqlTypeConstant.VARCHAR)
    private Long groupId;

    @TableField
    @Column(name = "review_position", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "审核立场")
    private String reviewPosition;

    @TableField
    @Column(name = "doc_type", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "文档类型")
    private String docType;

    @TableField
    @Column(name = "scope", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "适用范围")
    private String scope;

    @TableField
    @Column(name = "rule_description", type = MySqlTypeConstant.VARCHAR, length = 500, comment = "规则描述")
    private String ruleDescription;
}