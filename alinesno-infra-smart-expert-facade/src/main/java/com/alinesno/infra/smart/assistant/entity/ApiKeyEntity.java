package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("api_keys") // MyBatis-Plus 表名注解
public class ApiKeyEntity extends InfraBaseEntity {

    @TableField(value = "app_name") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("所属应用")
    private String appName ; // 所属应用

    @TableField(value = "key_content") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("密钥内容")
    private String keyContent; // 密钥内容

    @TableField("created_at") // MyBatis-Plus 字段注解
    @ColumnComment("创建时间")
    private Long createdAt; // 创建时间

    @TableField("expires_at") // MyBatis-Plus 字段注解
    @ColumnComment("过期时间")
    private Long expiresAt; // 过期时间

    @TableField("is_active") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.INT, length = 1)
    @ColumnComment("是否激活")
    private Integer isActive; // 是否激活

    // 其他字段和方法...
}
