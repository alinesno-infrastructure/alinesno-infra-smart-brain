package com.alinesno.infra.smart.inference.agent.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDate;

/**
 * 智能体计划类列表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("life_agent_plan")
@Data
@NoArgsConstructor
public class AgentPlanEntity extends InfraBaseEntity {

    @TableField
    private long agentId ; // 智能体ID

    @TableField
    private long goalId ; // 目标ID

    @TableField
    private String title; // 标题

    @TableField
    private String description; // 描述

    @TableField
    private LocalDate dueDate; // 截止日期

    @TableField
    private boolean isCompleted; // 是否完成

    @TableField
    private int priority;  // 优先级

}