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
// * 用户指令收藏实体类
// *
// * 表名：user_prompt_collection
// *
// * 字段注释：
// * - 用户标识：用户标识
// * - 收集资源：收集资源
// * - 排序：排序
// *
// * 功能名：指令收藏
// *
// * @author luoxiaodong
// * @version 1.0.0
// */
//@EqualsAndHashCode(callSuper = true)
//@Data
//@TableName("user_prompt_collection")
//public class PromptCollectionEntity extends InfraBaseEntity {
//
//	// fields
//	/**
//	 * 用户标识
//	 */
//	@Excel(name = "用户标识")
//	@TableField("user_id")
//	@ColumnType(length=50)
//	@ColumnComment("用户标识")
//	private Long userId;
//	/**
//	 * 收集资源
//	 */
//	@Excel(name = "收集资源")
//	@TableField("collected_prompt")
//	@ColumnType(length=255)
//	@ColumnComment("收集资源")
//	private String collectedPrompt;
//	/**
//	 * 排序
//	 */
//	@Excel(name = "排序")
//	@TableField("sorting_order")
//	@ColumnType(length=1)
//	@ColumnComment("排序")
//	private Long sortingOrder;
//}
