package com.alinesno.infra.smart.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * 消息反馈表实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("message_feedback")
@TableComment("消息反馈表")
public class MessageFeedbackEntity extends InfraBaseEntity {

    @NotNull(message = "messageId字段不能为null")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 64, comment = "关联的消息ID")
    @TableField
    private String messageId;

    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "评价用户ID")
    @TableField
    private Long evaluatorUserId;  // 评价者用户ID

    @Column(type = MySqlTypeConstant.VARCHAR, length = 100, comment = "评价用户名称")
    @TableField
    private String evaluatorUserName;  // 评价者用户名称

    @Column(type = MySqlTypeConstant.BIT, comment = "反馈感受")
    @TableField
    private Boolean feel;

    @Column(type = MySqlTypeConstant.VARCHAR , length=512, comment = "反馈原因列表")
    @TableField
    private String reasons;

    @NotNull(message = "timestamp字段不能为null")
    @Column(type = MySqlTypeConstant.DATETIME, comment = "反馈时间")
    @TableField
    private Date timestamp;
}