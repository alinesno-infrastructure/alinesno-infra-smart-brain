package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ai_gen_task")
public class AiGenTaskEntity extends InfraBaseEntity {

    @TableField
    private String businessId ; // 业务ID

    @TableField
    private String promptId ; // GPT角色ID

    @TableField
    private String systemContent ;  // GPT角色设定

    @TableField
    private String userContent ;  // GPT用户信息

    @TableField
    private int taskStatus ;  // 0排队中/1运行中/2已完成/9失败

}
