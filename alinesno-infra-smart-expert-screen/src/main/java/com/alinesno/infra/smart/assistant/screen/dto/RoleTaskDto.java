package com.alinesno.infra.smart.assistant.screen.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleTaskDto extends BaseDto {

    private Long leaderRoleId ; // 领导角色ID
    private Long workerRoleId; // 工作者角色ID
    private String executeResult ; // 执行结果
    private Long screenId ; // 所属场景
    private String errorMessage ;
    private Integer isFinished ; // 是否执行结束，由LeaderRole判断

    private String roleName ; // 角色名称
    private String taskGoal ; // 任务目标
    private String taskDesc ; // 任务描述
    private String preRoleId ; // 需要等待任务完成的职工Id(对应上面的workerRoleId)

    private IndustryRoleEntity leaderRole ;
    private IndustryRoleEntity workerRole ;

}