package com.alineson.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * Prompt指令集实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("prompt_prompts")
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

	// getter and setter methods

	public Long getUseCount() {
		return useCount;
	}

	public void setUseCount(Long useCount) {
		this.useCount = useCount;
	}

	public String getPromptAuthor() {
		return promptAuthor;
	}

	public void setPromptAuthor(String promptAuthor) {
		this.promptAuthor = promptAuthor;
	}

	public String getPromptContent() {
		return promptContent;
	}

	public void setPromptContent(String promptContent) {
		this.promptContent = promptContent;
	}

	public String getPromptName() {
		return promptName;
	}

	public void setPromptName(String promptName) {
		this.promptName = promptName;
	}

	public String getPromptPassword() {
		return promptPassword;
	}

	public void setPromptPassword(String promptPassword) {
		this.promptPassword = promptPassword;
	}

	public Long getPromptStatus() {
		return promptStatus;
	}

	public void setPromptStatus(Long promptStatus) {
		this.promptStatus = promptStatus;
	}

	public String getPromptTitle() {
		return promptTitle;
	}

	public void setPromptTitle(String promptTitle) {
		this.promptTitle = promptTitle;
	}

	public String getPromptType() {
		return promptType;
	}

	public void setPromptType(String promptType) {
		this.promptType = promptType;
	}

	public String getToPing() {
		return toPing;
	}

	public void setToPing(String toPing) {
		this.toPing = toPing;
	}
}