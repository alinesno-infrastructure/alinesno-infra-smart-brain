package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 项目管理DTO类，用于产品研究场景
 * 继承自BaseDto以获取基础的DTO功能
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectManagerDTO extends BaseDto {
    /**
     * 定时任务的策略，用于确定如何执行项目同步任务
     */
    private String timingStrategy;

    /**
     * 项目的仓库URL，用于访问和同步项目代码
     */
    private String projectRepoUrl;

    /**
     * 项目类型，标识项目的技术栈或构建工具
     */
    private String projectType;

    /**
     * 认证用户名，用于访问受保护的项目仓库
     */
    private String authUsername;

    /**
     * 上次同步的时间，用于跟踪项目同步的状态
     */
    private Date lastSyncTime;

    /**
     * 同步间隔，单位未指定，用于确定同步任务的频率
     */
    private Integer syncInterval;

    /**
     * 项目状态，描述项目当前的同步或配置状态
     */
    private String status;
}
