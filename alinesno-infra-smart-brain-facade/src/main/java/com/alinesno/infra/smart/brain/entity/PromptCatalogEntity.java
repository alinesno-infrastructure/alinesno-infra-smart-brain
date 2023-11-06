package com.alinesno.infra.smart.brain.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Prompt指令类型实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("prompt_catalog")
public class PromptCatalogEntity extends InfraBaseEntity {

	// fields

	/**
	 * 行业分类
	 */
	@Excel(name="行业分类")
	@TableField("industry_category")
	@ColumnType(length=50)
	@ColumnComment("行业分类")
	private String industryCategory;

	/**
	 * 业务分类
	 */
	@Excel(name="业务分类")
	@TableField("business_category")
	@ColumnType(length=50)
	@ColumnComment("业务分类")
	private String businessCategory;

	/**
	 * 管理对象
	 */
	@Excel(name="管理对象")
	@TableField("management_object")
	@ColumnType(length=255)
	@ColumnComment("管理对象")
	private String managementObject;

	/**
	 * 信息类别
	 */
	@Excel(name="信息类别")
	@TableField("information_type")
	@ColumnType(length=50)
	@ColumnComment("信息类别")
	private String informationType;

	/**
	 * 主题分类
	 */
	@Excel(name="主题分类")
	@TableField("subject_category")
	@ColumnType(length=50)
	@ColumnComment("主题分类")
	private String subjectCategory;

	/**
	 * 资源提供方
	 */
	@Excel(name="资源提供方")
	@TableField("resource_provider")
	@ColumnType(length=50)
	@ColumnComment("资源提供方")
	private String resourceProvider;

	/**
	 * 来源系统
	 */
	@Excel(name="来源系统")
	@TableField("source_system")
	@ColumnType(length=50)
	@ColumnComment("来源系统")
	private String sourceSystem;

	/**
	 * 来源数据表
	 */
	@Excel(name="来源数据表")
	@TableField("source_data_table")
	@ColumnType(length=255)
	@ColumnComment("来源数据表")
	private String sourceDataTable;

	/**
	 * 状态
	 */
	@Excel(name="状态")
	@TableField("status")
	@ColumnType(length=1)
	@ColumnComment("状态")
	private String status;
}
