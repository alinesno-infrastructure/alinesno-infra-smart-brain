package com.alinesno.infra.smart.brain.console.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.brain.console.dto.LoginBody;
import com.alinesno.infra.smart.brain.console.menus.MenuConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = UUID.randomUUID().toString() ;
        ajax.put(TOKEN, token);
        return ajax;
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
    public AjaxResult getRouters()
    {

        String menusJson = "[\n" +
                "        {\n" +
                "            \"name\": \"Dashboard\",\n" +
                "            \"path\": \"/dashboard\",\n" +
                "            \"hidden\": false,\n" +
                "            \"redirect\": \"noRedirect\",\n" +
                "            \"component\": \"Layout\",\n" +
                "            \"alwaysShow\": true,\n" +
                "            \"meta\": {\n" +
                "                \"title\": \"概览\",\n" +
                "                \"icon\": \"tool\",\n" +
                "                \"noCache\": false,\n" +
                "                \"link\": null\n" +
                "            },\n" +
                "            \"children\": [\n" +
                "                {\n" +
                "                    \"name\": \"Index\",\n" +
                "                    \"path\": \"index\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"仪盘表\",\n" +
                "                        \"icon\": \"build\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"System\",\n" +
                "            \"path\": \"/system\",\n" +
                "            \"hidden\": false,\n" +
                "            \"redirect\": \"noRedirect\",\n" +
                "            \"component\": \"Layout\",\n" +
                "            \"alwaysShow\": true,\n" +
                "            \"meta\": {\n" +
                "                \"title\": \"系统管理\",\n" +
                "                \"icon\": \"system\",\n" +
                "                \"noCache\": false,\n" +
                "                \"link\": null\n" +
                "            },\n" +
                "            \"children\": [\n" +
                "                {\n" +
                "                    \"name\": \"User\",\n" +
                "                    \"path\": \"user\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"system/user/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"用户管理\",\n" +
                "                        \"icon\": \"user\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Role\",\n" +
                "                    \"path\": \"role\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"system/role/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"角色管理\",\n" +
                "                        \"icon\": \"peoples\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Menu\",\n" +
                "                    \"path\": \"menu\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"system/menu/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"菜单管理\",\n" +
                "                        \"icon\": \"tree-table\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Dept\",\n" +
                "                    \"path\": \"dept\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"system/dept/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"部门管理\",\n" +
                "                        \"icon\": \"tree\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Post\",\n" +
                "                    \"path\": \"post\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"system/post/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"岗位管理\",\n" +
                "                        \"icon\": \"post\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Dict\",\n" +
                "                    \"path\": \"dict\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"system/dict/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"字典管理\",\n" +
                "                        \"icon\": \"dict\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Config\",\n" +
                "                    \"path\": \"config\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"system/config/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"参数设置\",\n" +
                "                        \"icon\": \"edit\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Notice\",\n" +
                "                    \"path\": \"notice\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"system/notice/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"通知公告\",\n" +
                "                        \"icon\": \"message\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Log\",\n" +
                "                    \"path\": \"log\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"redirect\": \"noRedirect\",\n" +
                "                    \"component\": \"ParentView\",\n" +
                "                    \"alwaysShow\": true,\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"日志管理\",\n" +
                "                        \"icon\": \"log\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    },\n" +
                "                    \"children\": [\n" +
                "                        {\n" +
                "                            \"name\": \"Operlog\",\n" +
                "                            \"path\": \"operlog\",\n" +
                "                            \"hidden\": false,\n" +
                "                            \"component\": \"monitor/operlog/index\",\n" +
                "                            \"meta\": {\n" +
                "                                \"title\": \"操作日志\",\n" +
                "                                \"icon\": \"form\",\n" +
                "                                \"noCache\": false,\n" +
                "                                \"link\": null\n" +
                "                            }\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"name\": \"Logininfor\",\n" +
                "                            \"path\": \"logininfor\",\n" +
                "                            \"hidden\": false,\n" +
                "                            \"component\": \"monitor/logininfor/index\",\n" +
                "                            \"meta\": {\n" +
                "                                \"title\": \"登录日志\",\n" +
                "                                \"icon\": \"logininfor\",\n" +
                "                                \"noCache\": false,\n" +
                "                                \"link\": null\n" +
                "                            }\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"Monitor\",\n" +
                "            \"path\": \"/monitor\",\n" +
                "            \"hidden\": false,\n" +
                "            \"redirect\": \"noRedirect\",\n" +
                "            \"component\": \"Layout\",\n" +
                "            \"alwaysShow\": true,\n" +
                "            \"meta\": {\n" +
                "                \"title\": \"系统监控\",\n" +
                "                \"icon\": \"monitor\",\n" +
                "                \"noCache\": false,\n" +
                "                \"link\": null\n" +
                "            },\n" +
                "            \"children\": [\n" +
                "                {\n" +
                "                    \"name\": \"Online\",\n" +
                "                    \"path\": \"online\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"monitor/online/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"在线用户\",\n" +
                "                        \"icon\": \"online\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Job\",\n" +
                "                    \"path\": \"job\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"monitor/job/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"定时任务\",\n" +
                "                        \"icon\": \"job\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Druid\",\n" +
                "                    \"path\": \"druid\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"monitor/druid/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"数据监控\",\n" +
                "                        \"icon\": \"druid\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Server\",\n" +
                "                    \"path\": \"server\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"monitor/server/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"服务监控\",\n" +
                "                        \"icon\": \"server\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Cache\",\n" +
                "                    \"path\": \"cache\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"monitor/cache/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"缓存监控\",\n" +
                "                        \"icon\": \"redis\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"CacheList\",\n" +
                "                    \"path\": \"cacheList\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"monitor/cache/list\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"缓存列表\",\n" +
                "                        \"icon\": \"redis-list\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"Tool\",\n" +
                "            \"path\": \"/tool\",\n" +
                "            \"hidden\": false,\n" +
                "            \"redirect\": \"noRedirect\",\n" +
                "            \"component\": \"Layout\",\n" +
                "            \"alwaysShow\": true,\n" +
                "            \"meta\": {\n" +
                "                \"title\": \"系统工具\",\n" +
                "                \"icon\": \"tool\",\n" +
                "                \"noCache\": false,\n" +
                "                \"link\": null\n" +
                "            },\n" +
                "            \"children\": [\n" +
                "                {\n" +
                "                    \"name\": \"Build\",\n" +
                "                    \"path\": \"build\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"tool/build/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"表单构建\",\n" +
                "                        \"icon\": \"build\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Gen\",\n" +
                "                    \"path\": \"gen\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"tool/gen/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"代码生成\",\n" +
                "                        \"icon\": \"code\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Swagger\",\n" +
                "                    \"path\": \"swagger\",\n" +
                "                    \"hidden\": false,\n" +
                "                    \"component\": \"tool/swagger/index\",\n" +
                "                    \"meta\": {\n" +
                "                        \"title\": \"系统接口\",\n" +
                "                        \"icon\": \"swagger\",\n" +
                "                        \"noCache\": false,\n" +
                "                        \"link\": null\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "    ]" ;

        JSONArray menusArr = JSONArray.parseArray(menusJson) ;

        return AjaxResult.success(menusArr) ;
    }
}
