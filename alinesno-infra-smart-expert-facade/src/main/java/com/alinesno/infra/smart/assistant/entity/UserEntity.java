package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user")
public class UserEntity extends InfraBaseEntity {
	/**
	 * 用户ID
	 */
	@ColumnType(MySqlTypeConstant.BIGINT)
	@ColumnComment("用户ID")
	@TableField("user_id")
	private Long userId;

	/**
	 * 用户名
	 */
	@ColumnType(length = 50)
	@ColumnComment("用户名")
	@TableField("username")
	private String username;

	/**
	 * 密码
	 */
	@ColumnType(length = 50)
	@ColumnComment("密码")
	@TableField("password")
	private String password;

	/**
	 * 手机号
	 */
	@ColumnType(length = 20)
	@ColumnComment("手机号")
	@TableField("phone")
	private String phone;

	/**
	 * 邮箱
	 */
	@ColumnType(length = 50)
	@ColumnComment("邮箱")
	@TableField("email")
	private String email;

	/**
	 * 创建时间
	 */
	@ColumnType(value = MySqlTypeConstant.DATETIME, length = 18)
	@ColumnComment("创建时间")
	@TableField("create_time")
	private Date createTime;


}
