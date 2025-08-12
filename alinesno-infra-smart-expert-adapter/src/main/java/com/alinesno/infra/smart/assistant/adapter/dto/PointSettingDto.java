package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
 * PointSettingDto类
 */
@Data
public class PointSettingDto {
    private Integer maxSceneCount; // 最大场景数
    private Integer maxTaskCount; // 最大任务数
    private Integer currentPoint; // 当前积分
    private Integer remainingPoint; // 剩余积分
    private Integer totalConsumption; // 总消耗
}