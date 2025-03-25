package com.alinesno.infra.smart.assistant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 该类代表编辑状态下的行业角色实体，也就是未发布的角色信息。
 * 它继承自 InfraBaseEntity，因此拥有父类的基本属性。
 * 此实体类对应数据库中的 industry_role_draft 表。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("industry_role_draft") // MyBatis-Plus 表名注解
public class IndustryRoleDraftEntity extends IndustryRoleEntity {

    @TableField("draft_version") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.INT, length = 11)
    @ColumnComment("草稿版本号")
    private Integer draftVersion;
}