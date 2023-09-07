package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * 来源系统实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("source_system")
public class SourceSystemEntity extends InfraBaseEntity {
    // fields

    /**
     * 来源系统
     */
    @Excel(name="来源系统")
    @TableField("source_system")
    private String sourceSystem;

    /**
     * 系统标识
     */
    @Excel(name="系统标识")
    @TableField("system_id")
    private Long systemId;

    // getter and setter methods

    public String getSourceSystem() {
        return this.sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public Long getSystemId() {
        return this.systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }
}