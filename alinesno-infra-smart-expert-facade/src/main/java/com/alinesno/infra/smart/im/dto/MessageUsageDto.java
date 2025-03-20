package com.alinesno.infra.smart.im.dto;

import lombok.Data;

/**
 * MessageUsageDto类，用时间、token使用情况等数据。
 */
@Data
public class MessageUsageDto {
    private long time ; // 秒
    private long token ; // 消耗token数据
}
