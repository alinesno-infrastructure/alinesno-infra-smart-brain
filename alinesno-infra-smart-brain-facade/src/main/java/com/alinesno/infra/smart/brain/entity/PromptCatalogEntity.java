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

	@Excel(name="类型图标")
	@TableField("icon")
	@ColumnType(length=64)
	@ColumnComment("类型图标")
	private String icon;

	@Excel(name="类型名称")
	@TableField("name")
	@ColumnType(length=50)
	@ColumnComment("类型名称")
	private String name ;

	@Excel(name="类型描述")
	@TableField("description")
	@ColumnType(length=128)
	@ColumnComment("类型描述")
	private String description;

	@Excel(name="生成式数量")
	@TableField("prompt_count")
	@ColumnType(length=5)
	@ColumnComment("生成式数量")
	private int promptCount;

	@Excel(name="父类ID")
	@TableField("parent_id")
	@ColumnType(length=32)
	@ColumnComment("父类ID")
	private Long parentId;
}
