package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user_prompt_collection")
public class PromptCollectionEntity extends InfraBaseEntity {

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

}