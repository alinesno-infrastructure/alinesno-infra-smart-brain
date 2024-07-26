package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 技能实体类，用于表示不同技能的信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("skill_table") // MyBatis-Plus注解
public class SkillEntity extends InfraBaseEntity {

    @ColumnComment("角色ID")
    @ColumnType(value = MySqlTypeConstant.BIGINT)
    @TableField("role_id") // MyBatis-Plus注解
    private Long roleId;

    @ColumnComment("技能名称")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    private String skillName;

    @ColumnComment("技能描述")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    private String description;

    @ColumnComment("技能数据来源")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @TableField("data_loader") // MyBatis-Plus注解
    private String dataLoader;
}