package com.alinesno.infra.smart.inference.agent.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * 智能体目标实体类
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("life_agent_goal")
@Data
@NoArgsConstructor
public class AgentGoalEntity extends InfraBaseEntity {

    @TableField
    private long agentId; // 智能体ID

    @TableField
    private String title; // 目标标题

    @TableField
    private String description; // 目标描述

    @TableField
    private String priority; // 优先级

}