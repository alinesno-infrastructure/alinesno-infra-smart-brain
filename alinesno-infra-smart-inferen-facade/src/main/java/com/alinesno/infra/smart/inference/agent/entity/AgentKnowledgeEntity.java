package com.alinesno.infra.smart.inference.agent.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * 智能体知识库，每个智能体有多个智能库
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("life_agent_knowledge")
@Data
@NoArgsConstructor
public class AgentKnowledgeEntity extends InfraBaseEntity {

    @TableField
    private long agentId ; // 智能体ID

    @TableField
    private long knowledgeId ; // 知识库ID

    @TableField
    private String indexName ;   // 索引名称

}