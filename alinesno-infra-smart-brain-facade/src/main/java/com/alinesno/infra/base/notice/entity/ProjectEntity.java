package com.alinesno.infra.base.notice.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Project Entity
 * 
 * @author LuoXiaoDong
 * @version 1.0.0
 */
@TableName("project")
public class ProjectEntity extends InfraBaseEntity {
 
	@TableField("type")
	private String type; // 应用类型

	@TableField("version")
	private String version; // 应用版本号

	@TableField("name")
	private String name; // 应用名称

	@TableField("banner")
	private String banner; // 标识图标

	@TableField("icon")
	private String icon; // 应用icon

	@TableField("remark")
	private String remark; // 应用备注

	@TableField("open_key")
	private String openKey; // 应用代码

	@TableField("code")
	private String code; // 应用代码
 
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOpenKey() {
		return openKey;
	}

	public void setOpenKey(String openKey) {
		this.openKey = openKey;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
 
}