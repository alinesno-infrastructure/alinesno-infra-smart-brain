package com.alinesno.infra.smart.im.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * FeedbackDto 类
 * 用于接收用户评价
 */
@Data
public class MessageFeedbackDto {

    // 还有消息ID
    @NotNull(message = "messageId字段不能为null")
    private String messageId;

    @NotNull(message = "feel字段不能为null")
    private Boolean feel;

    @NotEmpty(message = "reasons列表不能为空")
    private List<@NotBlank(message = "reason不能为空字符串") String> reasons;

    @NotNull(message = "timestamp字段不能为null")
    @JsonProperty("timestamp")
    private Date timestamp;
}