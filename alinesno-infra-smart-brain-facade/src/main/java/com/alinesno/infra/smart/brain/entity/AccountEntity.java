//package com.alinesno.infra.smart.brain.entity;
//
//import cn.afterturn.easypoi.excel.annotation.Excel;
//import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
//import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
///**
// * 用户账户实体类
// *
// * @version 1.0.0
// * @author luoxiaodong
// */
//@EqualsAndHashCode(callSuper = true)
//@Data
//@TableName("user_account")
//public class AccountEntity extends InfraBaseEntity {
//
//    // fields
//
//    /**
//     * 用户状态
//     */
//    @Excel(name="用户状态")
//    @TableField("account_status")
//	@ColumnType(length=255)
//	@ColumnComment("用户状态")
//    private Long accountStatus;
//
//    /**
//     * 用户描述
//     */
//    @TableField
//	@ColumnType(length=255)
//	@ColumnComment("用户描述")
//    private String description; // 用户描述
//
//    /**
//     * 用户名称
//     */
//    @TableField
//	@ColumnType(length=255)
//	@ColumnComment("用户名称")
//    private String username; // 用户名称
//
//    /**
//     * 用户头像
//     */
//    @TableField
//	@ColumnType(length=255)
//	@ColumnComment("用户头像")
//    private String avatar ; // 用户头像
//
//    /**
//     * 登陆账号
//     */
//    @Excel(name="登陆账号")
//    @TableField("login_account")
//	@ColumnType(length=20)
//	@ColumnComment("登陆账号")
//    private String loginAccount;
//
//    /**
//     * 登陆授权码
//     */
//    @Excel(name="登陆密码")
//    @TableField("login_password")
//	@ColumnType(length=255)
//	@ColumnComment("登陆授权码")
//    private String loginPassword;
//
//    /**
//     * 手机号
//     */
//    @Excel(name="手机号")
//    @TableField("phone_code")
//	@ColumnType(length=4)
//	@ColumnComment("手机号")
//    private String phoneCode;
//
//    /**
//     * 加密
//     */
//    @Excel(name="加密")
//    @TableField("salt")
//	@ColumnType(length=64)
//	@ColumnComment("加密")
//    private String salt;
//
//    /**
//     * 询问次数
//     */
//    @TableField
//	@ColumnType(length=10)
//	@ColumnComment("询问次数")
//    private int askCount ; // 询问次数
//
//    /**
//     * 总的次数
//     */
//    @TableField
//	@ColumnType(length=10)
//	@ColumnComment("总的次数")
//    private int totalCount ; // 总的次数
//
//    /**
//     * 支付次数
//     */
//    @TableField
//	@ColumnType(length=2)
//	@ColumnComment("支付次数")
//    private int payCount ; // 支付次数
//}
