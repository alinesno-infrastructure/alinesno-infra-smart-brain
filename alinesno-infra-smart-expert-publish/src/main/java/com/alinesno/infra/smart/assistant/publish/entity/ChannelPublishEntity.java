package com.alinesno.infra.smart.assistant.publish.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发布渠道实体类，用于定义发布渠道的基础信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("role_publish")
@TableComment(value = "发布渠道表")
public class ChannelPublishEntity extends InfraBaseEntity implements Serializable {

    @TableField(value = "role_id")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("角色ID")
    private Long roleId;

    @TableField(value = "name")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("渠道名称")
    private String name;

    @TableField(value = "description")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("渠道描述")
    private String description;

    @TableField(value = "icon_class")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("渠道图标类名")
    private String iconClass;

    @TableField(value = "param_key")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("渠道参数键")
    private String paramKey;

    @TableField(value = "has_configured")
    @ColumnType(value = MySqlTypeConstant.TINYINT)
    @ColumnComment("是否已配置")
    private int hasConfigured;

    @TableField(value = "param_map")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 512)
    @ColumnComment("渠道参数键")
    private String configMap ;

    @TableField(value = "api_key")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 64)
    @ColumnComment("分享ApiKey参数")
    private String apiKey;

}