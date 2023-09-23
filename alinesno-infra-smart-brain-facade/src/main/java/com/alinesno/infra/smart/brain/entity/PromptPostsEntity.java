package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Prompt指令集实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("prompt_posts")
public class PromptPostsEntity extends InfraBaseEntity {

	// fields

	/**
	 * 指令使用次数
	 */
	@Excel(name="使用次数")
	@TableField("use_count")
	private Long useCount;

	/**
	 * 指令管理员
	 */
	@Excel(name="指令管理员")
	@TableField("prompt_author")
	private String promptAuthor;

	/**
	 * 指令内容
	 */
	@Excel(name="指令内容")
	@TableField("prompt_content")
	private String promptContent;

	/**
	 * 指令名称
	 */
	@Excel(name="指令名称")
	@TableField("prompt_name")
	private String promptName;

	/**
	 * 指令密钥
	 */
	@Excel(name="指令密钥")
	@TableField("prompt_password")
	private String promptPassword;

	/**
	 * 指令状态
	 */
	@Excel(name="指令状态")
	@TableField("prompt_status")
	private Long promptStatus;

	/**
	 * 指令标题
	 */
	@Excel(name="指令标题")
	@TableField("prompt_title")
	private String promptTitle;

	/**
	 * 指令类型
	 */
	@Excel(name="指令类型")
	@TableField("prompt_type")
	private String promptType;

	/**
	 * 对外
	 */
	@Excel(name="对外")
	@TableField("to_ping")
	private String toPing;

}