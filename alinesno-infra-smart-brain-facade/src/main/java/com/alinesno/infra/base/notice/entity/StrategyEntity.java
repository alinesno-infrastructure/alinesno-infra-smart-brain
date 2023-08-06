package com.alinesno.infra.base.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;

/**
 * 发送策略对象
 * @author luoxiaodong
 * @since 1.0.0
 */
@TableName("strategy") // 指定数据库表名
public class StrategyEntity extends InfraBaseEntity {

    // 策略名称
    @TableField("strategy_name")
    private String strategyName;

    // 策略ID
    @TableField("strategy_id")
    private String strategyId;

    // 重复发送次数
    @TableField("retry_count")
    private Integer retryCount;

    // 定时任务
    @TableField("scheduled_task")
    private String scheduledTask;

    // 发送批次
    @TableField("send_batch")
    private String sendBatch;

    // 消息是否备份
    @TableField("backup_message")
    private Boolean backupMessage;

    // 延迟发送时间
    @TableField("delay_send_time")
    private Long delaySendTime;

    // 是否报警
    @TableField("enable_alert")
    private Boolean enableAlert;

    // 限流方式（每分钟发送的条数）
    @TableField("rate_limit")
    private Integer rateLimit;

    // 省略了构造函数、getter 和 setter 方法

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getScheduledTask() {
        return scheduledTask;
    }

    public void setScheduledTask(String scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public String getSendBatch() {
        return sendBatch;
    }

    public void setSendBatch(String sendBatch) {
        this.sendBatch = sendBatch;
    }

    public Boolean getBackupMessage() {
        return backupMessage;
    }

    public void setBackupMessage(Boolean backupMessage) {
        this.backupMessage = backupMessage;
    }

    public Long getDelaySendTime() {
        return delaySendTime;
    }

    public void setDelaySendTime(Long delaySendTime) {
        this.delaySendTime = delaySendTime;
    }

    public Boolean getEnableAlert() {
        return enableAlert;
    }

    public void setEnableAlert(Boolean enableAlert) {
        this.enableAlert = enableAlert;
    }

    public Integer getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(Integer rateLimit) {
        this.rateLimit = rateLimit;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }
}
