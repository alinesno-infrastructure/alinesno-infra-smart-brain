package com.alinesno.infra.smart.assistant.workplace.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作台元素实体类，主要记录工作台中元素的相关信息，
 * 元素类型包括智能体、频道、场景，同时记录元素的排序信息，继承自 InfraBaseEntity 类。
 */
@EqualsAndHashCode(callSuper = true)
@TableName("workplace_item")
@Data
public class WorkplaceItemEntity extends InfraBaseEntity {

    /**
     * 工作台的唯一
     */
    @TableField
    @Column(name = "workplace_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "工作台的唯一标识")
    private long workplaceId ;

    /**
     * 元素的唯一标识
     */
    @TableField
    @Column(name = "item_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "元素的唯一标识")
    private long itemId;

    /**
     * 元素的类型，取值为智能体、频道、场景
     */
    @TableField
    @Column(name = "item_type", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "元素的类型，取值为智能体、频道、场景")
    private String itemType;

    /**
     * 元素的排序值
     */
    @TableField
    @Column(name = "sort_order", type = MySqlTypeConstant.INT, comment = "元素的排序值")
    private Integer sortOrder = 0 ;
}