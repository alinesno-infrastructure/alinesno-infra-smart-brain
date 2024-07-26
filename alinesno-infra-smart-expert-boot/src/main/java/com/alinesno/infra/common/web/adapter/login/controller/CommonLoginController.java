package com.alinesno.infra.common.web.adapter.login.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.dto.LoginBodyDto;
import com.alinesno.infra.common.web.adapter.dto.menus.Menu;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class CommonLoginController {

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBodyDto loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = UUID.randomUUID().toString() ;
        ajax.put(TOKEN, token);
        return ajax;
    }

    /**
     * 退出登陆
     * @return
     */
    @PostMapping("/logout")
    public AjaxResult logout() {
        StpUtil.logout();
        return AjaxResult.success();
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {

        Map<String, Object> data = new HashMap<>();
        // 将数据填充到data中...
        data.put("permissions", new String[]{"*:*:*"});

        Map<String, Object> user = new HashMap<>();
        user.put("createBy", "admin");
        user.put("createTime", "2023-04-23 16:11:38");
        user.put("updateBy", null);
        user.put("updateTime", null);
        user.put("remark", "管理员");
        user.put("userId", 1);
        user.put("deptId", 103);
        user.put("userName", "admin");
        user.put("nickName", "AIP技术团队");
        user.put("email", "aip-team@163.com");
        user.put("phonenumber", "15888888888");
        user.put("sex", "1");
        user.put("avatar", "");
        user.put("password", "");
        user.put("status", "0");
        user.put("delFlag", "0");
        user.put("loginIp", "");
        user.put("loginDate", "2023-09-21T16:54:12.000+08:00");

        Map<String, Object> dept = new HashMap<>();
        dept.put("createBy", null);
        dept.put("createTime", null);
        dept.put("updateBy", null);
        dept.put("updateTime", null);
        dept.put("remark", null);
        dept.put("deptId", 103);
        dept.put("parentId", 101);
        dept.put("ancestors", "0,100,101");
        dept.put("deptName", "研发部门");
        dept.put("orderNum", 1);
        dept.put("leader", "AIP技术团队");
        dept.put("phone", null);
        dept.put("email", null);
        dept.put("status", "0");
        dept.put("delFlag", null);
        dept.put("parentName", null);
        dept.put("children", new Object[]{});

        user.put("dept", dept);

        Map<String, Object> role = new HashMap<>();
        role.put("createBy", null);
        role.put("createTime", null);
        role.put("updateBy", null);
        role.put("updateTime", null);
        role.put("remark", null);
        role.put("roleId", 1);
        role.put("roleName", "超级管理员");
        role.put("roleKey", "admin");
        role.put("roleSort", 1);
        role.put("dataScope", "1");
        role.put("menuCheckStrictly", false);
        role.put("deptCheckStrictly", false);
        role.put("status", "0");
        role.put("delFlag", null);
        role.put("flag", false);
        role.put("menuIds", null);
        role.put("deptIds", null);
        role.put("permissions", null);
        role.put("admin", true);

        user.put("roles", new Object[]{role});

        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", user.get("roles"));
        ajax.put("permissions", data.get("permissions"));

        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {

        Menu dashboardMenu = new Menu("Dashboard", "/dashboard", false, "noRedirect", "Layout", true, new Menu.Meta("仪盘表", "dashboard", false, null), List.of(
                new Menu("Dashboard", "index", false, false , "dashboard", new Menu.Meta("概览", "dashboard", false, null)) ,
                new Menu("Project", "smart/brain/application/index", false, false , "smart/brain/application/index", new Menu.Meta("项目管理", "monitor", false, null))
        ));

        Menu expertMenu = new Menu("Expert", "/expert", false, "noRedirect", "Layout", true, new Menu.Meta("专家配置", "post", false, null),
                List.of(
                        new Menu("Role", "smart/assistant/role/index", false,false,  "smart/assistant/role/index", new Menu.Meta("专家管理", "tree", false, null)),
                        new Menu("RoleCatalog", "smart/assistant/roleCatalog/index", false, false, "smart/assistant/roleCatalog/index", new Menu.Meta("专家类型", "post", false, null)),
                        new Menu("ChainScript", "smart/assistant/chainScript/index", false,false,  "smart/assistant/chainScript/index", new Menu.Meta("脚本定义", "dict", false, null)),
                        new Menu("WorkflowRecord", "smart/assistant/workflowRecord/index", false,false,  "smart/assistant/workflowRecord/index", new Menu.Meta("流程监控", "message", false, null)),
                        new Menu("Channel", "smart/assistant/channel/index", false, false, "smart/assistant/channel/index", new Menu.Meta("专家插件", "peoples", false, null))
                ));

        List<Menu> menus = getMenus(dashboardMenu, expertMenu);

        return AjaxResult.success(menus) ;
    }

    @NotNull
    private static List<Menu> getMenus(Menu dashboardMenu, Menu expertMenu) {
        Menu promptMenu = new Menu("Prompt", "/prompt", false, "noRedirect", "Layout", true, new Menu.Meta("提示管理", "log", false, null),
                        List.of(
                                new Menu("Prompt", "smart/brain/prompt/index", false,false, "smart/brain/prompt/index", new Menu.Meta("定义Prompt", "message", false, null)),
                                new Menu("Task", "smart/brain/task/index", false,false, "smart/brain/task/index", new Menu.Meta("生成任务", "peoples", false, null)),
                                new Menu("Catalog", "smart/brain/catalog/index", false,false, "smart/brain/catalog/index", new Menu.Meta("Prompt目录", "form", false, null))
                        ));

        Menu monitorMenu = new Menu("Monitor", "/monitor", false, "noRedirect", "Layout", true, new Menu.Meta("监控管理", "log", false, null),
                List.of(
                        new Menu("ApiRecord", "smart/assistant/apiRecord/index", false,false, "smart/assistant/apiRecord/indexx", new Menu.Meta("操作日志", "message", false, null)),
                        new Menu("Analyse", "smart/assistant/analyse/index", false,false, "smart/assistant/analyse/index", new Menu.Meta("接口监控", "server", false, null))
                ));

        List<Menu> menus = List.of(dashboardMenu, expertMenu, promptMenu, monitorMenu);
        return menus;
    }
}
