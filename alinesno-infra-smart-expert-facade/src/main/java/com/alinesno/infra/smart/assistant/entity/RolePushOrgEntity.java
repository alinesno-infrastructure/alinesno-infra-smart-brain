package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织角色(用于推送角色信息，一个角色可以推送给多个组织)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("industry_role_push_org") // MyBatis-Plus 表名注解
public class RolePushOrgEntity extends InfraBaseEntity {

    @TableField("role_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("角色ID")
    private Long roleId;

    @TableField("org_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("组织ID")
    private Long orgId;

    // 是否被录用
    @TableField("is_accepted")
    @Column(type = MySqlTypeConstant.SMALLINT, length = 1)
    @ColumnComment("是否被录用")
    private boolean isAccepted;

    // 覆盖录用还是直接录用
    @TableField("is_cover")
    @Column(type = MySqlTypeConstant.SMALLINT, length = 1)
    @ColumnComment("是否覆盖")
    private boolean isCover;

}
