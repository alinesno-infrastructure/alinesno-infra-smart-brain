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
 * 来源系统实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("source_system")
public class SourceSystemEntity extends InfraBaseEntity {
    // fields

    /**
     * 来源系统
     */
    @Excel(name="来源系统")
    @TableField("source_system")
	@ColumnType(length=50)
	@ColumnComment("来源系统")
    private String sourceSystem;

    /**
     * 系统标识
     */
    @Excel(name="系统标识")
    @TableField("system_id")
	@ColumnType(length=50)
	@ColumnComment("系统标识")
    private Long systemId;
}
