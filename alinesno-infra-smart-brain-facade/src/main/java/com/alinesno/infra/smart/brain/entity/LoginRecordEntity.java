package com.alinesno.infra.smart.brain.entity;
import java.util.Date;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户登陆记录实体类
 *
 * 表名：user_login_record
 *
 * 字段注释：
 * - 邮箱验证码：邮箱验证码
 * - 错误密码：错误密码
 * - 登录浏览器：登录浏览器
 * - 登录IP：登录IP
 * - 登录名称：登录名称
 * - 登录备注：登录备注
 * - 登录来源：登录来源
 * - 登录状态：登录状态
 * - 登录时间：登录时间
 * - 手机验证码：手机验证码
 * - 退出时间：退出时间
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user_login_record")
public class LoginRecordEntity extends InfraBaseEntity{

    @Excel(name="邮箱验证码") // emailCode字段对应的Excel列名为"邮箱验证码"
    @TableField("email_code")
    private String emailCode;

    @Excel(name="错误密码") // errorPassword字段对应的Excel列名为"错误密码"
    @TableField("error_password")
    private String errorPassword;

    @Excel(name="登录浏览器") // loginBrowser字段对应的Excel列名为"登录浏览器"
    @TableField("login_browser")
    private String loginBrowser;

    @Excel(name="登录IP") // loginIp字段对应的Excel列名为"登录IP"
    @TableField("login_ip")
    private String loginIp;

    @Excel(name="登录名称") // loginName字段对应的Excel列名为"登录名称"
    @TableField("login_name")
    private String loginName;

    @Excel(name="登录备注") // loginRemark字段对应的Excel列名为"登录备注"
    @TableField("login_remark")
    private String loginRemark;

    @Excel(name="登录来源") // loginSrc字段对应的Excel列名为"登录来源"
    @TableField("login_src")
    private String loginSrc;

    @Excel(name="登录状态") // loginStatus字段对应的Excel列名为"登录状态"
    @TableField("login_status")
    private String loginStatus;

    @Excel(name="登录时间") // loginTime字段对应的Excel列名为"登录时间"
    @TableField("login_time")
    private Date loginTime;

    @Excel(name="手机验证码") // phoneCode字段对应的Excel列名为"手机验证码"
    @TableField("phone_code")
    private String phoneCode;

    @Excel(name="退出时间") // signOutTime字段对应的Excel列名为"退出时间"
    @TableField("sign_out_time")
    private Date signOutTime;

}