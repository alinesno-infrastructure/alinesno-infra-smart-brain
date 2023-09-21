package com.alinesno.infra.smart.brain.console.menus;

import java.util.List;

public class MenuConverter {
        public static List<Menu> convertToAjaxResult() {
            Menu systemMenu = new Menu(
                    "System",
                    "/system",
                    false,
                    "noRedirect",
                    "Layout",
                    true,
                    new Menu.Meta("系统管理", "system", false, null),
                    List.of(
                            new Menu(
                                    "User",
                                    "user",
                                    false,
                                    "system/user/index",
                                    new Menu.Meta("用户管理", "user", false, null) //  public Meta(String title, String icon, boolean noCache, String link)
                            ),
                            new Menu(
                                    "Role",
                                    "role",
                                    false,
                                    "system/role/index",
                                    new Menu.Meta("角色管理", "peoples", false, null)
                            ),
                            new Menu(
                                    "Menu",
                                    "menu",
                                    false,
                                    "system/menu/index",
                                    new Menu.Meta("菜单管理", "tree-table", false, null)
                            ),
                            new Menu(
                                    "Dept",
                                    "dept",
                                    false,
                                    "system/dept/index",
                                    new Menu.Meta("部门管理", "tree", false, null)
                            ),
                            new Menu(
                                    "Post",
                                    "post",
                                    false,
                                    "system/post/index",
                                    new Menu.Meta("岗位管理", "post", false, null)
                            ),
                            new Menu(
                                    "Dict",
                                    "dict",
                                    false,
                                    "system/dict/index",
                                    new Menu.Meta("字典管理", "dict", false, null)
                            ),
                            new Menu(
                                    "Config",
                                    "config",
                                    false,
                                    "system/config/index",
                                    new Menu.Meta("参数设置", "edit", false, null)
                            ),
                            new Menu(
                                    "Notice",
                                    "notice",
                                    false,
                                    "system/notice/index",
                                    new Menu.Meta("通知公告", "message", false, null)
                            ),
                            new Menu(
                                    "Log",
                                    "log",
                                    false,
                                    "noRedirect",
                                    "ParentView",
                                    true,
                                    new Menu.Meta("日志管理", "log", false, null),
                                    List.of(
                                            new Menu(
                                                    "Operlog",
                                                    "operlog",
                                                    false,
                                                    "monitor/operlog/index",
                                                    new Menu.Meta("操作日志", "form", false, null)
                                            ),
                                            new Menu(
                                                    "Logininfor",
                                                    "logininfor",
                                                    false,
                                                    "monitor/logininfor/index",
                                                    new Menu.Meta("登录日志", "logininfor", false, null)
                                            )
                                    )
                            )
                    )
            );

            Menu monitorMenu = new Menu(
                    "Monitor",
                    "/monitor",
                    false,
                    "noRedirect",
                    "Layout",
                    true,
                    new Menu.Meta("系统监控", "monitor", false, null),
                    List.of(
                            new Menu(
                                    "Online",
                                    "online",
                                    false,
                                    "monitor/online/index",
                                    new Menu.Meta("在线用户", "online", false, null)
                            ),
                            new Menu(
                                    "Job",
                                    "job",
                                    false,
                                    "monitor/job/index",
                                    new Menu.Meta("定时任务", "job", false, null)
                            ),
                            new Menu(
                                    "Druid",
                                    "druid",
                                    false,
                                    "monitor/druid/index",
                                    new Menu.Meta("数据监控", "druid", false, null)
                            ),
                            new Menu(
                                    "Server",
                                    "server",
                                    false,
                                    "monitor/server/index",
                                    new Menu.Meta("服务监控", "server", false, null)
                            ),
                            new Menu(
                                    "Cache",
                                    "cache",
                                    false,
                                    "monitor/cache/index",
                                    new Menu.Meta("缓存监控", "redis", false, null)
                            ),
                            new Menu(
                                    "CacheList",
                                    "cacheList",
                                    false,
                                    "monitor/cache/list",
                                    new Menu.Meta("缓存列表", "redis-list", false, null)
                            )
                    )
            );

            Menu toolMenu = new Menu(
                    "Tool",
                    "/tool",
                    false,
                    "noRedirect",
                    "Layout",
                    true,
                    new Menu.Meta("系统工具", "tool", false, null),
                    List.of(
                            new Menu(
                                    "Build",
                                    "build",
                                    false,
                                    "tool/build/index",
                                    new Menu.Meta("表单构建", "build", false, null)
                            ),
                            new Menu(
                                    "Gen",
                                    "gen",
                                    false,
                                    "tool/gen/index",
                                    new Menu.Meta("代码生成", "code", false, null)
                            ),
                            new Menu(
                                    "Swagger",
                                    "swagger",
                                    false,
                                    "tool/swagger/index",
                                    new Menu.Meta("系统接口", "swagger", false, null)
                            )
                    )
            );

            Menu httpMenu = new Menu(
                    "Http://ruoyi.vip",
                    "http://ruoyi.vip",
                    false,
                    "Layout",
                    new Menu.Meta("若依官网", "guide", false, "http://ruoyi.vip")
            );
            List<Menu> menus = List.of(systemMenu, monitorMenu, toolMenu, httpMenu);

            return menus;
        }
}
