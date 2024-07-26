package com.alinesno.infra.smart.assistant.role.yaml;

import lombok.Data;

import java.util.List;

/**
 * 接口控制层类实现
 */
@Data
public class RestControllerBean {

    private List<ControllerApiBean> controllers ; // 控制层Controller接口

    // 控制层API接口类
    @Data
    public static class ControllerApiBean {
        private String name ; // 控制层的名称
        private String desc ; // 控制层描述，主要描述这个接口控制层的作用
        private String endPoint ; // 控制层的Request请求地址
        private List<ApiBean> apis ; // 接口列表对象
    }

    // 接口信息
    @Data
    public static class ApiBean {
        private String name ;  // 接口名称
        private String desc ; // 接口描述

        private String methodName ; // 方法名称
        private String requestType ; // 方法请求类型(POST/GET/DELETE/PUT)
        private String endPoint ; // 请求地址

        private List<RequestParamDtoBean> requestParams ; // 请求参数
        private List<ResponseParamDtoBean> responseParams ; // 返回请求参数

        private String successMsg ; // 成功返回提示
        private String errorMsg ; // 异常返回提示
    }

    // 接口请求参数
    @Data
    public static class RequestParamDtoBean {
        private String type ; // 参数类型
        private String name ; // 参数名称
        private String desc ; // 参数描述
    }

    // 接口请求参数
    @Data
    public static class ResponseParamDtoBean {
        private String type ; // 参数类型
        private String name ; // 参数名称
        private String desc ; // 参数描述
    }
}
