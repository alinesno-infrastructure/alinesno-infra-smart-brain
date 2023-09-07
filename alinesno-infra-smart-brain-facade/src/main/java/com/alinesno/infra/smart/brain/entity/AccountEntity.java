package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * 用户账户实体类
 *
 * @version 1.0.0
 * @author luoxiaodong
 */
@TableName("user_account")
public class AccountEntity extends InfraBaseEntity {

    // fields

    /**
     * 用户状态
     */
    @Excel(name="用户状态")
    @TableField("account_status")
    private Long accountStatus;

    /**
     * 用户描述
     */
    @TableField
    private String description; // 用户描述

    /**
     * 用户名称
     */
    @TableField
    private String username; // 用户名称

    /**
     * 用户头像
     */
    @TableField
    private String avatar ; // 用户头像

    /**
     * 登陆账号
     */
    @Excel(name="登陆账号")
    @TableField("login_account")
    private String loginAccount;

    /**
     * 登陆授权码
     */
    @Excel(name="登陆密码")
    @TableField("login_password")
    private String loginPassword;

    /**
     * 手机号
     */
    @Excel(name="手机号")
    @TableField("phone_code")
    private String phoneCode;

    /**
     * 加密
     */
    @Excel(name="加密")
    @TableField("salt")
    private String salt;

    /**
     * 询问次数
     */
    @TableField
    private int askCount ; // 询问次数

    /**
     * 总的次数
     */
    @TableField
    private int totalCount ; // 总的次数

    /**
     * 支付次数
     */
    @TableField
    private int payCount ; // 支付次数

    // getter and setter methods

    public Long getAccountStatus() {
        return this.accountStatus;
    }

    public void setAccountStatus(Long accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAskCount() {
        return askCount;
    }

    public void setAskCount(int askCount) {
        this.askCount = askCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPayCount() {
        return payCount;
    }

    public void setPayCount(int payCount) {
        this.payCount = payCount;
    }

    public String getLoginAccount() {
        return this.loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getLoginPassword() {
        return this.loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getPhoneCode() {
        return this.phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}