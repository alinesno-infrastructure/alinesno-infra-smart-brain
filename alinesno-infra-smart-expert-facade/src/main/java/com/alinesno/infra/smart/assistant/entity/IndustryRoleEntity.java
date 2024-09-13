package com.alinesno.infra.smart.assistant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行业角色实体类，用于表示不同行业中的角色信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("industry_role") // MyBatis-Plus 表名注解
public class IndustryRoleEntity extends InfraBaseEntity {

    @TableField("role_avatar") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("角色名称")
    private String roleAvatar; // 角色头像

    @TableField("role_name") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("角色名称")
    private String roleName; // 角色名称

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 32)
    @ColumnComment("关联的ChainID")
    @TableField("chain_id") // MyBatis-Plus 字段注解
    private String chainId; // 其他角色相关字段

    @TableField("industry_catalog") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("所属行业")
    private Long industryCatalog ; // 所属行业

    @TableField("responsibilities") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("角色职责描述")
    private String responsibilities; // 角色职责描述

    @TableField("role_level") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("角色等级")
    private String roleLevel; // 角色等级

    @TableField("other_attributes") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("其他角色相关字段")
    private String otherAttributes; // 其他角色相关字段

	@Excel(name="指令内容")
	@TableField("prompt_content")
	@ColumnType(value = MySqlTypeConstant.LONGTEXT)
	@ColumnComment("指令内容")
	private String promptContent;

    @Excel(name="会议次数")
    @TableField("chat_count")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 11)
    @ColumnComment("会话次数")
    private Long chatCount = 0L ;
}