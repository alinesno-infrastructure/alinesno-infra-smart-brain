package com.alinesno.infra.smart.assistant.role.yaml;

import com.alinesno.infra.smart.assistant.role.utils.YAMLMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestControllerCaseBeanExample {

    public static void main(String[] args) throws IOException {
        RestControllerCaseBean testCases = new RestControllerCaseBean();

        List<RestControllerCaseBean.CaseBean> cases = new ArrayList<>();

        // 用例1: 正常登录
        RestControllerCaseBean.CaseBean case1 = new RestControllerCaseBean.CaseBean();
        case1.setType("正常登录");
        case1.setDesc("使用正确的账号、密码和验证码进行登录");

        List<RestControllerCaseBean.RequestParamDtoBean> requestParams1 = new ArrayList<>();
        RestControllerCaseBean.RequestParamDtoBean accountParam1 = new RestControllerCaseBean.RequestParamDtoBean();
        accountParam1.setType("String");
        accountParam1.setName("account");
        accountParam1.setValue("user@example.com");  // 设置参数值
        accountParam1.setDesc("账号（手机号或邮箱）");
        requestParams1.add(accountParam1);

        RestControllerCaseBean.RequestParamDtoBean passwordParam1 = new RestControllerCaseBean.RequestParamDtoBean();
        passwordParam1.setType("String");
        passwordParam1.setName("password");
        passwordParam1.setValue("password123");  // 设置参数值
        passwordParam1.setDesc("密码");
        requestParams1.add(passwordParam1);

        RestControllerCaseBean.RequestParamDtoBean verificationCodeParam1 = new RestControllerCaseBean.RequestParamDtoBean();
        verificationCodeParam1.setType("String");
        verificationCodeParam1.setName("verificationCode");
        verificationCodeParam1.setValue("123456");  // 设置参数值
        verificationCodeParam1.setDesc("验证码");
        requestParams1.add(verificationCodeParam1);

        case1.setRequestParams(requestParams1);

        List<RestControllerCaseBean.ResponseParamDtoBean> expectedResponse1 = new ArrayList<>();
        RestControllerCaseBean.ResponseParamDtoBean userParam1 = new RestControllerCaseBean.ResponseParamDtoBean();
        userParam1.setType("User");
        userParam1.setName("user");
        userParam1.setValue("{\"name\": \"John Doe\", \"email\": \"user@example.com\"}");  // 设置参数值
        userParam1.setDesc("用户信息");
        expectedResponse1.add(userParam1);

        case1.setExpectedResponse(expectedResponse1);

        cases.add(case1);

        // 用例2: 安全测试 - 账号密码为空
        RestControllerCaseBean.CaseBean case2 = new RestControllerCaseBean.CaseBean();
        case2.setType("安全测试 - 账号密码为空");
        case2.setDesc("尝试使用空的账号和密码进行登录");

        List<RestControllerCaseBean.RequestParamDtoBean> requestParams2 = new ArrayList<>();
        requestParams2.add(accountParam1);  // 使用上面定义的accountParam1
        requestParams2.add(passwordParam1);  // 使用上面定义的passwordParam1

        case2.setRequestParams(requestParams2);
        case2.setExpectedResponse(expectedResponse1);  // 期望返回相同的用户信息

        cases.add(case2);

        // 用例3: 特殊情况 - 验证码错误
        RestControllerCaseBean.CaseBean case3 = new RestControllerCaseBean.CaseBean();
        case3.setType("特殊情况 - 验证码错误");
        case3.setDesc("使用正确的账号和密码，但提供错误的验证码进行登录");

        List<RestControllerCaseBean.RequestParamDtoBean> requestParams3 = new ArrayList<>();
        requestParams3.add(accountParam1);  // 使用上面定义的accountParam1
        requestParams3.add(passwordParam1);  // 使用上面定义的passwordParam1

        RestControllerCaseBean.RequestParamDtoBean wrongVerificationCodeParam = new RestControllerCaseBean.RequestParamDtoBean();
        wrongVerificationCodeParam.setType("String");
        wrongVerificationCodeParam.setName("verificationCode");
        wrongVerificationCodeParam.setValue("654321");  // 设置错误的验证码
        wrongVerificationCodeParam.setDesc("验证码");
        requestParams3.add(wrongVerificationCodeParam);

        case3.setRequestParams(requestParams3);
        case3.setExpectedResponse(expectedResponse1);  // 期望返回相同的用户信息

        cases.add(case3);

        // 用例4: 异常情况 - 缺少账号参数
        RestControllerCaseBean.CaseBean case4 = new RestControllerCaseBean.CaseBean();
        case4.setType("异常情况 - 缺少账号参数");
        case4.setDesc("尝试登录时缺少必需的账号参数");

        List<RestControllerCaseBean.RequestParamDtoBean> requestParams4 = new ArrayList<>();
        requestParams4.add(passwordParam1);  // 使用上面定义的passwordParam1
        requestParams4.add(verificationCodeParam1);  // 使用上面定义的verificationCodeParam1

        case4.setRequestParams(requestParams4);
        case4.setExpectedResponse(expectedResponse1);  // 期望返回相同的用户信息

        cases.add(case4);

        // 用例: SQL注入测试 - 恶意输入
        RestControllerCaseBean.CaseBean case5 = new RestControllerCaseBean.CaseBean();
        case5.setType("SQL注入测试 - 恶意输入");
        case5.setDesc("尝试使用恶意输入进行SQL注入攻击");

        List<RestControllerCaseBean.RequestParamDtoBean> requestParams5 = new ArrayList<>();
        requestParams5.add(accountParam1);  // 使用上面定义的accountParam1
        requestParams5.add(passwordParam1);  // 使用上面定义的passwordParam1
        requestParams5.add(verificationCodeParam1);  // 使用上面定义的verificationCodeParam1

        // 构造恶意输入的账号参数
        accountParam1.setValue("' OR 1=1; --");

        case5.setRequestParams(requestParams5);
        case5.setExpectedResponse(expectedResponse1);  // 期望返回相同的用户信息

        cases.add(case5);
        testCases.setCases(cases);

        // 输出示例对象
        System.out.println(YAMLMapper.toYAML(testCases));
    }

}
