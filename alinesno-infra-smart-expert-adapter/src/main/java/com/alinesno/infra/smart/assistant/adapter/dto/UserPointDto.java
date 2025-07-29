package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
 * 用户积分DTO，账户id/已用积分/剩余积分/用户等级
 */
@Data
public class UserPointDto {
    private Long accountId;  // 用户ID
    private int usedPoint;  // 已用积分
    private Integer points;   // 用户当前积分（注意字段名是points不是pointValue）
    private String userLevel;  // 用户等级
}
