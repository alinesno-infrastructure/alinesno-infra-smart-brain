package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * 用户指令收藏实体类
 *
 * 表名：user_prompt_collection
 *
 * 字段注释：
 * - 用户标识：用户标识
 * - 收集资源：收集资源
 * - 排序：排序
 *
 * 功能名：指令收藏
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("user_prompt_collection")
public class PromptCollectionEntity extends InfraBaseEntity {
	private static final long serialVersionUID = 1L;
	// fields
	/**
	 * 用户标识
	 */
	@Excel(name = "用户标识")
	@TableField("user_id")
	private Long userId;
	/**
	 * 收集资源
	 */
	@Excel(name = "收集资源")
	@TableField("collected_prompt")
	private String collectedPrompt;
	/**
	 * 排序
	 */
	@Excel(name = "排序")
	@TableField("sorting_order")
	private Long sortingOrder;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCollectedPrompt() {
		return collectedPrompt;
	}

	public void setCollectedPrompt(String collectedPrompt) {
		this.collectedPrompt = collectedPrompt;
	}

	public Long getSortingOrder() {
		return sortingOrder;
	}

	public void setSortingOrder(Long sortingOrder) {
		this.sortingOrder = sortingOrder;
	}

	// getter and setter

}