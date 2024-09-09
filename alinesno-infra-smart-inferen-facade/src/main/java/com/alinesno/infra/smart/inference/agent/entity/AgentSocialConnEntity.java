package com.alinesno.infra.smart.inference.agent.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 智能社交连接实体类，关联的所有人物信息
 */
@TableName("life_agent_social_conn")
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentSocialConnEntity extends InfraBaseEntity {

    @TableField
    private long agentId ;

    @TableField
    private Date time;

    @TableField
    private String location;

    @TableField
    private int depth;

    @TableField(exist = false)
    private AgentSocialConnEntity next;

}