package com.alinesno.infra.smart.assistant.point.processor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 拦截的工具类
 */
public interface PointAnnotationProcessor {

    /**
     * 拦截解析用户操作积分
     * @param request
     * @param userId
     * @param orgId
     * @throws Exception
     */
    void process(HttpServletRequest request, Long userId , Long orgId) throws Exception;

}