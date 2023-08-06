package com.alineson.infra.smart.brain.entity;

import java.util.Date;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * 用户账户记录实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("user_account_record")
public class AccountRecordEntity extends InfraBaseEntity {

    // fields

    /**
     * 账户ID
     */
    @Excel(name="账户ID")
    @TableField("account_id")
    private String accountId;

    /**
     * 账户名称
     */
    @Excel(name="账户名称")
    @TableField("account_name")
    private String accountName;

    /**
     * 代理
     */
    @Excel(name="代理")
    @TableField("agent")
    private String agent;

    /**
     * 创建时间
     */
    @Excel(name="创建时间")
    @TableField("create_time")
    private Date createTime;

    /**
     * IP地址
     */
    @Excel(name="IP地址")
    @TableField("ip")
    private String ip;

    /**
     * 登录名
     */
    @Excel(name="登录名")
    @TableField("login_name")
    private String loginName;

    /**
     * 方法
     */
    @Excel(name="方法")
    @TableField("method")
    private String method;

    /**
     * 方法描述
     */
    @Excel(name="方法描述")
    @TableField("method_desc")
    private String methodDesc;

    /**
     * 方法时间
     */
    @Excel(name="方法时间")
    @TableField("method_time")
    private Long methodTime;

    /**
     * 操作
     */
    @Excel(name="操作")
    @TableField("operation")
    private String operation;

    /**
     * 参数
     */
    @Excel(name="参数")
    @TableField("params")
    private String params;

    /**
     * 角色权限
     */
    @Excel(name="角色权限")
    @TableField("role_power")
    private String rolePower;

    /**
     * URL
     */
    @Excel(name="URL")
    @TableField("url")
    private String url;

    /**
     * 记录类型
     */
    @Excel(name="记录类型")
    @TableField("record_type")
    private String recordType;

    // getter and setter

    public String getAccountId() {
        return this.accountId;
    }

    public AccountRecordEntity setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public AccountRecordEntity setAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public String getAgent() {
        return this.agent;
    }

    public AccountRecordEntity setAgent(String agent) {
        this.agent = agent;
        return this;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public AccountRecordEntity setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getIp() {
        return this.ip;
    }

    public AccountRecordEntity setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public AccountRecordEntity setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getMethod() {
        return this.method;
    }

    public AccountRecordEntity setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getMethodDesc() {
        return this.methodDesc;
    }

    public AccountRecordEntity setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
        return this;
    }

    public Long getMethodTime() {
        return this.methodTime;
    }

    public AccountRecordEntity setMethodTime(Long methodTime) {
        this.methodTime = methodTime;
        return this;
    }

    public String getOperation() {
        return this.operation;
    }

    public AccountRecordEntity setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public String getParams() {
        return this.params;
    }

    public AccountRecordEntity setParams(String params) {
        this.params = params;
        return this;
    }

    public String getRolePower() {
        return this.rolePower;
    }

    public AccountRecordEntity setRolePower(String rolePower) {
        this.rolePower = rolePower;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public AccountRecordEntity setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getRecordType() {
        return this.recordType;
    }

    public AccountRecordEntity setRecordType(String recordType) {
        this.recordType = recordType;
        return this;
    }
}