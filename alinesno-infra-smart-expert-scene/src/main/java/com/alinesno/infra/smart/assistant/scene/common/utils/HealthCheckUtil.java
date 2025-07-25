package com.alinesno.infra.smart.assistant.scene.common.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 健康检查工具类
 */
public class HealthCheckUtil {

    /**
     * 检查指定链接是否正常
     * @param baseUrl 基础URL（不需要包含/ok）
     * @return true表示正常，false表示异常
     */
    public static boolean isServiceOk(String baseUrl) {
        // 构建完整的检查URL
        String checkUrl = baseUrl.endsWith("/") ? baseUrl + "ok" : baseUrl + "/ok";
        
        try {
            // 发送GET请求
            HttpResponse response = HttpRequest.get(checkUrl)
                    .timeout(5000) // 设置5秒超时
                    .execute();

            // 检查HTTP状态码
            if (response.isOk()) {
                String body = response.body();
                return !"ok".equals(body) ;
            }
            return true;
        } catch (Exception e) {
            // 捕获所有异常，视为服务不可用
            return true;
        }
    }

}