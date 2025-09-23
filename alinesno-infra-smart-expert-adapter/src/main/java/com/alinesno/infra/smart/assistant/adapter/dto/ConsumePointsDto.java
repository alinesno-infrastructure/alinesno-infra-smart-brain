package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
 * 消耗积分DTO
 */
@Data
public class ConsumePointsDto {

    /**
     * 用户ID
     */
    private Long userId ;

    /**
     * 组织ID
     */
    private Long orgId;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 级别
     */
    private String level;

    /**
     * 角色ID
     */
    private Long roleId ;

    /**
     * 角色名称
     */
    private String roleName ;

    /**
     * 场景ID
     */
    private Long sceneId ;

    /**
     * 场景名称
     */
    private String sceneName ;

    /**
     * 场景任务ID
     */
    private Long taskId ;

    /**
     * 时间戳
     */
    private Long timestamp ;

}