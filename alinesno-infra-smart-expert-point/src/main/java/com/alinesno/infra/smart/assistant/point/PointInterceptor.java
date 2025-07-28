package com.alinesno.infra.smart.assistant.point;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 积分拦截器
 */
@Slf4j
public class PointInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 获取请求的URL地址
        String requestUrl = request.getRequestURI();
        
        // 2. 获取Sa-Token信息
        // 检查是否登录
        boolean isLogin = StpUtil.isLogin();
        
        if (isLogin) {
            // 获取当前登录用户的token值
            String tokenValue = StpUtil.getTokenValue();
            
            // 获取当前登录用户的ID
            Object loginId = StpUtil.getLoginId();
            
            // 获取当前登录用户的token剩余有效期
            long tokenTimeout = StpUtil.getTokenTimeout();
            
            // 获取当前登录用户的登录设备
            String loginDevice = StpUtil.getLoginDevice();
            
            // 这里可以打印或处理这些信息
            log.debug("===== 请求拦截信息 =====");
            log.debug("请求URL: " + requestUrl);
            log.debug("用户Token: " + tokenValue);
            log.debug("用户ID: " + loginId);
            log.debug("Token剩余有效期: " + tokenTimeout + "秒");
            log.debug("登录设备: " + loginDevice);
            
            // 也可以将这些信息放入request属性中，供后续使用
            request.setAttribute("tokenInfo", tokenValue);
            request.setAttribute("loginId", loginId);
        } else {
            log.debug("未登录用户访问: " + requestUrl);
        }
        
        // 返回true继续执行，返回false则中断请求
        return true;
    }
}