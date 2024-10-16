package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账号与应用关联
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_account")
public class ProjectAccountEntity extends InfraBaseEntity {

    @TableField("account_id")
    @ColumnType(length = 32)
    @ColumnComment("账户ID")
    private Long accountId;

    @TableField("app_order")
    @ColumnType
    @ColumnComment("应用排序")
    private Long appOrder;

}
