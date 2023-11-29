package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 应用构建实体类
 * </p>
 * @author luoxiaodong
 * @version 1.0.0
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("application")
public class ApplicationEntity extends InfraBaseEntity {

    /**
     * 应用名称
     */
    @ColumnType(length = 64)
    @ColumnComment("应用名称")
    @TableField("name")
    private String name;

    /**
     * 应用Icons
     */
    @ColumnType(length = 64)
    @ColumnComment("应用Icons")
    @TableField("icons")
    private String icons;

    /**
     * 所属领域
     */
    @ColumnType(length = 16)
    @ColumnComment("所属领域")
    @TableField("domain")
    private String domain;

    /**
     * 显示名称
     */
    @ColumnType(length = 32)
    @ColumnComment("显示名称")
    @TableField("show_name")
    private String showName;

    /**
     * 显示名称
     */
    @ColumnType(length = 32)
    @ColumnComment("应用密钥")
    @TableField("app_key")
    private String appKey;
}