package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户账户实体类
 *
 * @version 1.0.0
 * @author luoxiaodong
 */
@EqualsAndHashCode(callSuper = true)
@Data
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

}