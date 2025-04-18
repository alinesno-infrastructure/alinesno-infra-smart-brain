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
@TableName("document_review_rule_group")
@Data
public class DocReviewRuleGroupEntity extends InfraBaseEntity {

    // 分组名称、分组排序
    @TableField
    @Column(name = "group_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "分组名称")
    private String groupName;

    @TableField
    @Column(name = "group_sort", type = MySqlTypeConstant.INT, length = 5, comment = "分组排序")
    private Integer groupSort;

}
