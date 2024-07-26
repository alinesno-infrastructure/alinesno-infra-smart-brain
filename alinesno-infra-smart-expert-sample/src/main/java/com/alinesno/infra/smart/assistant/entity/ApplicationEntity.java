package com.alinesno.infra.smart.assistant.entity;

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
    @ColumnType(length = 50)
    @ColumnComment("应用名称")
    @TableField("name")
    private String name;

    /**
     * 应用Icons
     */
    @ColumnType(length = 100)
    @ColumnComment("应用Icons")
    @TableField("icons")
    private String icons;

    /**
     * 所属领域
     */
    @ColumnType(length = 50)
    @ColumnComment("所属领域")
    @TableField("domain")
    private String domain;

    /**
     * 显示名称
     */
    @ColumnType(length = 50)
    @ColumnComment("显示名称")
    @TableField("show_name")
    private String showName;

    /**
     * 域名
     */
    @ColumnType(length = 100)
    @ColumnComment("域名")
    @TableField("domain_name")
    private String domainName;

    /**
     * 安全存储路径
     */
    @ColumnType(length = 200)
    @ColumnComment("安全存储路径")
    @TableField("storage_path")
    private String storagePath;

    /**
     * 应用目标（k8s/docker/jar）
     */
    @ColumnType(length = 20)
    @ColumnComment("应用目标")
    @TableField("target")
    private String target;

    /**
     * 日志监控
     */
    @ColumnType(length = 50)
    @ColumnComment("日志监控")
    @TableField("logger_watch")
    private String loggerWatch;

    /**
     * Prometheus监控
     */
    @ColumnType(length = 50)
    @ColumnComment("Prometheus监控")
    @TableField("prothrombins_watch")
    private String prothrombinsWatch;
}