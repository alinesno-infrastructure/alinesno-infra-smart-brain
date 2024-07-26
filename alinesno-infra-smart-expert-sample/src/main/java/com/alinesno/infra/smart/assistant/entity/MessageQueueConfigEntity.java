package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息配置表，用于管理消息队列的参数设置。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("message_queue_config")
@TableComment("消息队列配置")
public class MessageQueueConfigEntity extends InfraBaseEntity {

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR , length = 32, comment = "业务标识ID")
    private String businessId ;

    @TableField
    @Column(type = MySqlTypeConstant.INT, comment = "最大重试次数")
    private int maxRetryAttempts;   // 最大重试次数

    @TableField
    @Column(type = MySqlTypeConstant.BIGINT, comment = "重试延迟时间（毫秒）")
    private long retryDelay; // 重试延迟时间（毫秒）

    @TableField
    @Column(type = MySqlTypeConstant.BIGINT, comment = "消息超时时间（毫秒）")
    private long messageTimeout; // 消息超时时间（毫秒）

    @TableField
    @Column(type = MySqlTypeConstant.INT, comment = "消息队列最大长度")
    private int maxQueueSize; // 消息队列最大长度
}
