package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审查清单
 */
@EqualsAndHashCode(callSuper = true)
@TableName("document_review_audit")
@Data
public class DocReviewAuditEntity extends InfraBaseEntity {

    // 审查清单名称、审核清单列表（以id做为数组保存)
    @TableField
    @Column(name = "audit_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "审核清单名称")
    private String auditName ;

    @TableField
    @Column(name = "audit_list", type = MySqlTypeConstant.LONGTEXT, comment = "审核清单列表")
    private String auditList ;

    // 数据范围（是否为公开或者为组织所用)
    @TableField
    @Column(name = "data_scope", type = MySqlTypeConstant.VARCHAR, length = 16, comment = "数据范围")
    private String dataScope ;

}
