package com.alinesno.infra.smart.assistant.point.processor;

import jakarta.servlet.http.HttpServletRequest;

public interface PointAnnotationProcessor {
    void process(HttpServletRequest request, Long userId) throws Exception;
}