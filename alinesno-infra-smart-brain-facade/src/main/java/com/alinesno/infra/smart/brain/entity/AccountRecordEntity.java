package com.alinesno.infra.smart.brain.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import java.util.Date;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户账户记录实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user_account_record")
public class AccountRecordEntity extends InfraBaseEntity {

    // fields

    /**
     * 账户ID
     */
    @Excel(name="账户ID")
    @TableField("account_id")
	@ColumnType(length=10)
	@ColumnComment("账户ID")
    private String accountId;

    /**
     * 账户名称
     */
    @Excel(name="账户名称")
    @TableField("account_name")
	@ColumnType(length=50)
	@ColumnComment("账户名称")
    private String accountName;

    /**
     * 代理
     */
    @Excel(name="代理")
    @TableField("agent")
	@ColumnType(length=255)
	@ColumnComment("代理")
    private String agent;

    /**
     * 创建时间
     */
    @Excel(name="创建时间")
    @TableField("create_time")
	@ColumnType(length=19)
	@ColumnComment("创建时间")
    private Date createTime;

    /**
     * IP地址
     */
    @Excel(name="IP地址")
    @TableField("ip")
	@ColumnType(length=15)
	@ColumnComment("IP地址")
    private String ip;

    /**
     * 登录名
     */
    @Excel(name="登录名")
    @TableField("login_name")
	@ColumnType(length=50)
	@ColumnComment("登录名")
    private String loginName;

    /**
     * 方法
     */
    @Excel(name="方法")
    @TableField("method")
	@ColumnType(length=255)
	@ColumnComment("方法")
    private String method;

    /**
     * 方法描述
     */
    @Excel(name="方法描述")
    @TableField("method_desc")
	@ColumnType(length=255)
	@ColumnComment("方法描述")
    private String methodDesc;

    /**
     * 方法时间
     */
    @Excel(name="方法时间")
    @TableField("method_time")
	@ColumnType(length=8)
	@ColumnComment("方法时间")
    private Long methodTime;

    /**
     * 操作
     */
    @Excel(name="操作")
    @TableField("operation")
	@ColumnType(length=255)
	@ColumnComment("操作")
    private String operation;

    /**
     * 参数
     */
    @Excel(name="参数")
    @TableField("params")
	@ColumnType(length=255)
	@ColumnComment("参数")
    private String params;

    /**
     * 角色权限
     */
    @Excel(name="角色权限")
    @TableField("role_power")
	@ColumnType(length=10)
	@ColumnComment("角色权限")
    private String rolePower;

    /**
     * URL
     */
    @Excel(name="URL")
    @TableField("url")
	@ColumnType(length=255)
	@ColumnComment("URL")
    private String url;

    /**
     * 记录类型
     */
    @Excel(name="记录类型")
    @TableField("record_type")
	@ColumnType(length=20)
	@ColumnComment("记录类型")
    private String recordType;
}
