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
@TableName("org_workplace")
@Data
public class OrgWorkplaceEntity extends InfraBaseEntity {

    /**
     * 工作台的唯一
     */
    @TableField
    @Column(name = "workplace_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "工作台的唯一标识")
    private long workplaceId ;

}