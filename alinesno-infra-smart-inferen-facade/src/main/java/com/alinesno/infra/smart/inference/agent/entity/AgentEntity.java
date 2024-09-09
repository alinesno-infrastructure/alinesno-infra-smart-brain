package com.alinesno.infra.smart.inference.agent.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 智能体实体类
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@TableName("life_agent")
@Data
@NoArgsConstructor
public class AgentEntity extends InfraBaseEntity {

    @TableField
    private String name; // 名称

    @TableField
    private int age; // 年龄

    @TableField
    private int experience; // 经验值

    @TableField
    private int memory; // 记忆能力值

    @TableField
    private int health; // 健康值

    @TableField
    private int level; // 当前级别

    @TableField
    private int growthRate; // 成长速率，每轮增加的年龄值

    @TableField
    private int maxAge; // 最大年龄

    @TableField
    private String jobDescription; // 工作描述

    @TableField
    private int maxExperience; // 经验值上限

    @TableField
    private boolean isHealthy; // 是否健康

    @TableField
    private String currentStage; // 当前生命阶段

    @TableField
    private String personality; // 性格特点

    @TableField
    private String familyEnvironment; // 家庭环境

    @TableField
    private String abilities; // 能力范围

    @TableField
    private String interests; // 喜好

    @TableField
    private String homeAddress; // 家庭住址

    @TableField
    private String educationSchool; // 教育学校

    @TableField
    private int startWorkAge; // 开始工作的年龄

    @TableField
    private int workYears; // 工作年限

    @TableField
    @ColumnType(value = MySqlTypeConstant.TEXT)
    private String attributes ; // 属性

}