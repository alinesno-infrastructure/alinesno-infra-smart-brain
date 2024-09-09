package com.alinesno.infra.smart.inference.agent.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * 智能体所执行的能力列表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("life_agent_ability")
@Data
@NoArgsConstructor
public class AgentAbilityEntity extends InfraBaseEntity {

    @TableField
    private long agentId ; // 智能体ID

    @TableField
    private long abilityCode ; // 能力代码

    @TableField
    private String name ; // 能力名称

    @TableField
    private String description ;// 能力描述

    @TableField
    private String type ;// 能力类型

}