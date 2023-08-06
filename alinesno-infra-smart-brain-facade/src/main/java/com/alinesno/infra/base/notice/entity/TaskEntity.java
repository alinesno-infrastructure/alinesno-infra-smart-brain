package com.alinesno.infra.base.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;

/**
 * 发送任务列表
 * @author luoxiaodong
 * @since 1.0.0
 */
@TableName("send_task") // 指定数据库表名
public class TaskEntity extends InfraBaseEntity {

    // 任务名称
    @TableField("task_name")
    private String taskName;

    // 定时CRON表达式
    @TableField("cron_expression")
    private String cronExpression;

    // 是否运行
    @TableField("is_running")
    private Boolean running;

    // 运行状态
    @TableField("status")
    private String status;

    // 省略了构造函数、getter 和 setter 方法

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
