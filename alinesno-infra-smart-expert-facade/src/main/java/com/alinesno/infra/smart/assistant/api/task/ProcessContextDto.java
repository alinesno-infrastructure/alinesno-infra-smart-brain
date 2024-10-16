package com.alinesno.infra.smart.assistant.api.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * 任务执行上下文DTO类
 * 用于在任务调度API中传递任务执行的相关信息
 */
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessContextDto {

    // 任务ID，用于标识任务
    private long id;

    // 任务名称，用于标识任务
    private String taskName;

    // 任务描述，用于描述任务的功能和用途
    private String taskDesc;

    // 任务模板
    private String dataCollectionTemplate ;

    // 上下文信息，可用于存储任务执行的额外信息
    private Map<String, String> globalParams;

    // 项目代码
    private long projectCode;

    // 环境ID
    private long envId;

    // 超时时间
    private int timeout;

    // 是否启用警报，用于控制任务执行异常时是否发送警报
    private boolean isAlertEnabled;

    // 监控邮箱，用于接收任务执行警报的电子邮件地址
    private String monitorEmail;

    // Cron表达式，用于定义任务执行的调度时间规则
    private String cronExpression;

    // 任务开始时间，用于指定任务开始执行的时间
    private String startTime;

    // 任务结束时间，用于指定任务结束执行的时间
    private String endTime;
}
