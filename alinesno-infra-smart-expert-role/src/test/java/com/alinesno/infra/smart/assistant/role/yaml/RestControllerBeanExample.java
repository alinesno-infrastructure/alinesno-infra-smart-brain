package com.alinesno.infra.smart.assistant.role.yaml;

import com.alinesno.infra.smart.assistant.role.utils.YAMLMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestControllerBeanExample {
    public static void main(String[] args) throws IOException {
        // 创建控制器列表
        List<RestControllerBean.ControllerApiBean> controllers = new ArrayList<>();

        // 创建控制器1
        RestControllerBean.ControllerApiBean controller1 = new RestControllerBean.ControllerApiBean();
        controller1.setName("UserController");
        controller1.setDesc("用户控制器");

        // 创建控制器1的API接口列表
        List<RestControllerBean.ApiBean> apiBeans1 = new ArrayList<>();

        // 创建API接口1
        RestControllerBean.ApiBean api1 = new RestControllerBean.ApiBean();
        api1.setName("获取用户信息");
        api1.setDesc("通过用户ID获取用户信息");
        api1.setMethodName("getUserInfo");
        api1.setRequestType("GET");
        api1.setEndPoint("/api/user/{id}");

        List<RestControllerBean.RequestParamDtoBean> requestParams1 = new ArrayList<>();
        RestControllerBean.RequestParamDtoBean requestParam1 = new RestControllerBean.RequestParamDtoBean();
        requestParam1.setType("String");
        requestParam1.setName("id");
        requestParam1.setDesc("用户ID");
        requestParams1.add(requestParam1);
        api1.setRequestParams(requestParams1);

        List<RestControllerBean.ResponseParamDtoBean> responseParams1 = new ArrayList<>();
        RestControllerBean.ResponseParamDtoBean responseParam1 = new RestControllerBean.ResponseParamDtoBean();
        responseParam1.setType("User");
        responseParam1.setName("user");
        responseParam1.setDesc("用户信息");
        responseParams1.add(responseParam1);
        api1.setResponseParams(responseParams1);

        api1.setSuccessMsg("成功获取用户信息");
        api1.setErrorMsg("无法获取用户信息");

        apiBeans1.add(api1);

        // 创建API接口2
        RestControllerBean.ApiBean api2 = new RestControllerBean.ApiBean();
        api2.setName("创建用户");
        api2.setDesc("创建新用户");
        api2.setMethodName("createUser");
        api2.setRequestType("POST");
        api2.setEndPoint("/api/user");

        List<RestControllerBean.RequestParamDtoBean> requestParams2 = new ArrayList<>();
        RestControllerBean.RequestParamDtoBean requestParam2 = new RestControllerBean.RequestParamDtoBean();
        requestParam2.setType("User");
        requestParam2.setName("user");
        requestParam2.setDesc("用户信息");
        requestParams2.add(requestParam2);
        api2.setRequestParams(requestParams2);

        List<RestControllerBean.ResponseParamDtoBean> responseParams2 = new ArrayList<>();
        RestControllerBean.ResponseParamDtoBean responseParam2 = new RestControllerBean.ResponseParamDtoBean();
        responseParam2.setType("User");
        responseParam2.setName("user");
        responseParam2.setDesc("创建的用户信息");
        responseParams2.add(responseParam2);
        api2.setResponseParams(responseParams2);

        api2.setSuccessMsg("成功创建用户");
        api2.setErrorMsg("无法创建用户");

        apiBeans1.add(api2);

        controller1.setApis(apiBeans1);

        controllers.add(controller1);

        // 创建控制器2
        RestControllerBean.ControllerApiBean controller2 = new RestControllerBean.ControllerApiBean();
        controller2.setName("ProductController");
        controller2.setDesc("产品控制器");

        // 创建控制器2的API接口列表
        List<RestControllerBean.ApiBean> apiBeans2 = new ArrayList<>();

        // 创建API接口3
        RestControllerBean.ApiBean api3 = new RestControllerBean.ApiBean();
        api3.setName("获取产品信息");
        api3.setDesc("通过产品ID获取产品信息");
        api3.setMethodName("getProductInfo");
        api3.setRequestType("GET");
        api3.setEndPoint("/api/product/{id}");

        List<RestControllerBean.RequestParamDtoBean> requestParams3 = new ArrayList<>();
        RestControllerBean.RequestParamDtoBean requestParam3 = new RestControllerBean.RequestParamDtoBean();
        requestParam3.setType("String");
        requestParam3.setName("id");
        requestParam3.setDesc("产品ID");
        requestParams3.add(requestParam3);
        api3.setRequestParams(requestParams3);

        List<RestControllerBean.ResponseParamDtoBean> responseParams3 = new ArrayList<>();
        RestControllerBean.ResponseParamDtoBean responseParam3 = new RestControllerBean.ResponseParamDtoBean();
        responseParam3.setType("Product");
        responseParam3.setName("product");
        responseParam3.setDesc("产品信息");
        responseParams3.add(responseParam3);
        api3.setResponseParams(responseParams3);

        api3.setSuccessMsg("成功获取产品信息");
        api3.setErrorMsg("无法获取产品信息");

        apiBeans2.add(api3);

        // 创建API接口4
        RestControllerBean.ApiBean api4 = new RestControllerBean.ApiBean();
        api4.setName("创建产品");
        api4.setDesc("创建新产品");
        api4.setMethodName("createProduct");
        api4.setRequestType("POST");
        api4.setEndPoint("/api/product");

        List<RestControllerBean.RequestParamDtoBean> requestParams4 = new ArrayList<>();
        RestControllerBean.RequestParamDtoBean requestParam4 = new RestControllerBean.RequestParamDtoBean();
        requestParam4.setType("Product");
        requestParam4.setName("product");
        requestParam4.setDesc("产品信息");
        requestParams4.add(requestParam4);
        api4.setRequestParams(requestParams4);

        List<RestControllerBean.ResponseParamDtoBean> responseParams4 = new ArrayList<>();
        RestControllerBean.ResponseParamDtoBean responseParam4 = new RestControllerBean.ResponseParamDtoBean();
        responseParam4.setType("Product");
        responseParam4.setName("product");
        responseParam4.setDesc("创建的产品信息");
        responseParams4.add(responseParam4);
        api4.setResponseParams(responseParams4);

        api4.setSuccessMsg("成功创建产品");
        api4.setErrorMsg("无法创建产品");

        apiBeans2.add(api4);

        controller2.setApis(apiBeans2);

        controllers.add(controller2);

        // 创建RestControllerBean对象
        RestControllerBean restControllerBean = new RestControllerBean();
        restControllerBean.setControllers(controllers);

        // 输出示例对象
        System.out.println(restControllerBean);
        // 输出示例对象
        System.out.println(YAMLMapper.toYAML(restControllerBean));

        String yamlContent = "controllers:\n" +
                "- name: \"UserController\"  # 控制器名称\n" +
                "  desc: \"用户控制器\"  # 控制器描述\n" +
                "  endPoint: \"/api/user\"  # 控制器的基础终端点\n" +
                "  apis:\n" +
                "  - name: \"用户登录\"  # API接口名称\n" +
                "    desc: \"通过账号密码和验证码进行登录\"  # API接口描述\n" +
                "    methodName: \"userLogin\"  # 方法名\n" +
                "    requestType: \"POST\"  # 请求类型\n" +
                "    endPoint: \"/login\"  # API接口的终端点\n" +
                "    requestParams:  # 请求参数列表\n" +
                "    - type: \"String\"  # 参数类型\n" +
                "      name: \"account\"  # 参数名\n" +
                "      desc: \"账号（手机号或邮箱）\"  # 参数描述\n" +
                "    - type: \"String\"\n" +
                "      name: \"password\"\n" +
                "      desc: \"密码\"\n" +
                "    - type: \"String\"\n" +
                "      name: \"verificationCode\"\n" +
                "      desc: \"验证码\"\n" +
                "    responseParams:  # 返回参数列表\n" +
                "    - type: \"User\"  # 参数类型\n" +
                "      name: \"user\"  # 参数名\n" +
                "      desc: \"用户信息\"  # 参数描述\n" +
                "    successMsg: \"登录成功\"  # 成功提示信息\n" +
                "    errorMsg: \"登录失败，账号或密码错误\"  # 错误提示信息\n" +
                "  - name: \"用户注册\"\n" +
                "    desc: \"通过手机号或邮箱验证进行用户注册\"\n" +
                "    methodName: \"userRegister\"\n" +
                "    requestType: \"POST\"\n" +
                "    endPoint: \"/register\"\n" +
                "    requestParams:\n" +
                "    - type: \"String\"\n" +
                "      name: \"account\"\n" +
                "      desc: \"账号（手机号或邮箱）\"\n" +
                "    - type: \"String\"\n" +
                "      name: \"verificationCode\"\n" +
                "      desc: \"验证码\"\n" +
                "    responseParams:\n" +
                "    - type: \"User\"\n" +
                "      name: \"user\"\n" +
                "      desc: \"注册成功的用户信息\"\n" +
                "    successMsg: \"注册成功\"\n" +
                "    errorMsg: \"注册失败，账号已存在或验证码错误\"\n" ;

        RestControllerBean restControllerBean1 = YAMLMapper.fromYAML(yamlContent , RestControllerBean.class) ;
        System.out.println("------------------------------------");
        System.out.println(restControllerBean1);
    }
}
