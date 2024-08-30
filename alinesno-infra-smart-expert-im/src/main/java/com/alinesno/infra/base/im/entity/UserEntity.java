package com.alinesno.infra.base.im.entity;

import com.alinesno.infra.base.im.enums.AccountType;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 用户实体详情类
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("account_info")
public class UserEntity extends InfraBaseEntity {

    @Column(type = MySqlTypeConstant.TINYINT, isNull = true, comment = "账号类型(0平台账号|1Agent账号|9其它类型)")
    private int accountType = AccountType.PLATFORM.getValue() ;  // 账号类型(0平台账号|1Agent账号|9其它类型)

    @Column(type = MySqlTypeConstant.BIGINT, length = 11, isNull = false, comment = "用户ID")
    private Long accountId;

    @Column(type = MySqlTypeConstant.VARCHAR , length = 50, isNull = false, comment = "用户名")
    private String accountName;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 100, isNull = true, comment = "用户头像存储路径")
    private String avatar;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 20, isNull = true, comment = "用户状态")
    private String status;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, isNull = true, comment = "Agent账户ID")
    private String agentId ; // Agent账户ID

    @TableField("role_name") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("角色名称")
    private String roleName; // 角色名称

    @TableField("industry") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("所属行业")
    private String industry; // 所属行业

    @TableField("responsibilities") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("角色职责描述")
    private String responsibilities; // 角色职责描述

    @TableField("skills") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("所需技能描述")
    private String skills; // 所需技能描述

    @TableField("experience") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("经验要求描述")
    private String experience; // 经验要求描述

    @TableField("role_level") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("角色等级")
    private String roleLevel; // 角色等级

    @TableField("salary_range") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("薪资范围")
    private String salaryRange; // 薪资范围

    @TableField("education") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("教育背景要求")
    private String education; // 教育背景要求

    @TableField("other_attributes") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("其他角色相关字段")
    private String otherAttributes; // 其他角色相关字段

    @ColumnType(value = MySqlTypeConstant.BIGINT , length = 32)
    @ColumnComment("关联的ChainID")
    @TableField("chain_id") // MyBatis-Plus 字段注解
    private Long chainId; // 其他角色相关字段

    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("关联的PromptId")
    @TableField("prompt_id") // MyBatis-Plus 字段注解
    private String promptId ; // 其他角色相关字段

}