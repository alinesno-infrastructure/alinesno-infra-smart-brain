package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * Prompt指令类型实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("prompt_catalog")
public class PromptCatalogEntity extends InfraBaseEntity {

	// fields

	/**
	 * 行业分类
	 */
	@Excel(name="行业分类")
	@TableField("industry_category")
	private String industryCategory;

	/**
	 * 业务分类
	 */
	@Excel(name="业务分类")
	@TableField("business_category")
	private String businessCategory;

	/**
	 * 管理对象
	 */
	@Excel(name="管理对象")
	@TableField("management_object")
	private String managementObject;

	/**
	 * 信息类别
	 */
	@Excel(name="信息类别")
	@TableField("information_type")
	private String informationType;

	/**
	 * 主题分类
	 */
	@Excel(name="主题分类")
	@TableField("subject_category")
	private String subjectCategory;

	/**
	 * 资源提供方
	 */
	@Excel(name="资源提供方")
	@TableField("resource_provider")
	private String resourceProvider;

	/**
	 * 来源系统
	 */
	@Excel(name="来源系统")
	@TableField("source_system")
	private String sourceSystem;

	/**
	 * 来源数据表
	 */
	@Excel(name="来源数据表")
	@TableField("source_data_table")
	private String sourceDataTable;

	/**
	 * 状态
	 */
	@Excel(name="状态")
	@TableField("status")
	private String status;

	// getter and setter methods

	public String getIndustryCategory() {
		return industryCategory;
	}

	public void setIndustryCategory(String industryCategory) {
		this.industryCategory = industryCategory;
	}

	public String getBusinessCategory() {
		return businessCategory;
	}

	public void setBusinessCategory(String businessCategory) {
		this.businessCategory = businessCategory;
	}

	public String getManagementObject() {
		return managementObject;
	}

	public void setManagementObject(String managementObject) {
		this.managementObject = managementObject;
	}

	public String getInformationType() {
		return informationType;
	}

	public void setInformationType(String informationType) {
		this.informationType = informationType;
	}

	public String getSubjectCategory() {
		return subjectCategory;
	}

	public void setSubjectCategory(String subjectCategory) {
		this.subjectCategory = subjectCategory;
	}

	public String getResourceProvider() {
		return resourceProvider;
	}

	public void setResourceProvider(String resourceProvider) {
		this.resourceProvider = resourceProvider;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getSourceDataTable() {
		return sourceDataTable;
	}

	public void setSourceDataTable(String sourceDataTable) {
		this.sourceDataTable = sourceDataTable;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}