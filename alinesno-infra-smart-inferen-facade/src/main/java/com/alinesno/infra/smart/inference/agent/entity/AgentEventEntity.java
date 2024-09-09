package com.alinesno.infra.smart.inference.agent.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Table;


/**
 * 智能体事件类
 */
@EqualsAndHashCode(callSuper = true)
@TableName("life_agent_event")
@Data
@NoArgsConstructor
public class AgentEventEntity extends InfraBaseEntity {

    @TableField
    private long agentId ; // 智能体ID

    @TableField
    private String code; // 事件代码

    @TableField
    private String subject;  // 主题

    @TableField
    private String predicate;  // 谓词

    @TableField
    private String object; // 对象

}