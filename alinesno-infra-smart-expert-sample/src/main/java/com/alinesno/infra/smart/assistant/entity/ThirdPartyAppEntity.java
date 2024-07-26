package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;


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
@TableName("third_party_app")
public class ThirdPartyAppEntity extends InfraBaseEntity {
	/**
	 * 应用ID
	 */
	@ColumnType(MySqlTypeConstant.BIGINT)
	@ColumnComment("应用ID")
	@TableField("app_id")
	private Long appId;

	/**
	 * 应用名称
	 */
	@ColumnType(length = 50)
	@ColumnComment("应用名称")
	@TableField("app_name")
	private String appName;

	/**
	 * 应用描述
	 */
	@ColumnType(MySqlTypeConstant.TEXT)
	@ColumnComment("应用描述")
	@TableField("app_description")
	private String appDescription;

	/**
	 * 接口地址
	 */
	@ColumnType(length = 100)
	@ColumnComment("接口地址")
	@TableField("api_url")
	private String apiUrl;


}
