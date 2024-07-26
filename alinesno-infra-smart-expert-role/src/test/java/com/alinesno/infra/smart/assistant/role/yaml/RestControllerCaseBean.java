package com.alinesno.infra.smart.assistant.role.yaml;

import lombok.Data;

import java.util.List;

/**
 * 接口测试用例
 */
@Data
public class RestControllerCaseBean {

    private List<CaseBean> cases ;

    @Data
    public static class CaseBean {
        private String type ; // 用例名称
        private String desc ; // 用例描述
        private String endPoint ; // 请求地址
        private String requestType ; // 接口请求类型

        private List<RequestParamDtoBean> requestParams ; // 请求参数
        private List<ResponseParamDtoBean> expectedResponse ; // 期望返回请求参数
    }

    // 接口请求参数
    @Data
    public static class RequestParamDtoBean {
        private String type ; // 参数类型
        private String name ; // 参数名称
        private String value ; // 参数值
        private String desc ; // 参数描述
    }

    // 接口请求参数
    @Data
    public static class ResponseParamDtoBean {
        private String type ; // 参数类型
        private String name ; // 参数名称
        private String value ; // 参数值
        private String desc ; // 参数描述
    }
}
