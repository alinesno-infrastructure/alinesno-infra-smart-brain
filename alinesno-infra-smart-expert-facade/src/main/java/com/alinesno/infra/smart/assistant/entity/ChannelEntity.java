package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName("channel")
public class ChannelEntity extends InfraBaseEntity {

    /**
     * 渠道名称
     */
    @ColumnType(length = 50)
    @ColumnComment("渠道名称")
    @TableField("name")
    private String name;

    @TableField("tool_type") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("工具类型：企业微信/钉钉/飞书等.")
    private String toolType; // 工具类型：企业微信/钉钉/飞书等

    @ColumnType(length = 50)
    @ColumnComment("机器人key")
    @TableField("robot_key")
    private String robotKey ;

    @ColumnType(length = 50)
    @ColumnComment("角色ID")
    @TableField("role_id")
    private long roleId;

    @TableField("api_key") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("工具的 API 密钥")
    private String apiKey; // 工具的 API 密钥

    @TableField("webhook_url") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("IM 工具的 Webhook URL")
    private String webhookUrl; // IM 工具的 Webhook URL

    @TableField("auth_token") // MyBatis-Plus 字段注解
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("认证信息")
    private String authToken; // 认证信息

    @ColumnComment("使用场景")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "screen")
    private String screen;

    @ColumnComment("描述信息")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "description")
    private String description;

}
