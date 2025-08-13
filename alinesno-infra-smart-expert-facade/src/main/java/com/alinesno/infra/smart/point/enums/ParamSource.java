package com.alinesno.infra.smart.point.enums;

/**
 * 参数来源枚举
 */
public enum ParamSource {
    /** 从 URL 的 query 参数中获取（如 /api?roleId=123） */
    PARAMS,
    /** 从请求体（JSON/Form-Data）中获取 */
    BODY
}