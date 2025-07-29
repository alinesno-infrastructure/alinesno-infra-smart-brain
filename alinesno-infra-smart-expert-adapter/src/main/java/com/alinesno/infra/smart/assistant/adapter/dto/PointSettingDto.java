package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
 * PointSettingDto类
 */
@Data
public class PointSettingDto {
    private String uri;        // 请求路径
    private String method;     // HTTP方法 (GET/POST等)
    private int pointValue;    // 所需积分
    private String pointName;  // 积分名称
}