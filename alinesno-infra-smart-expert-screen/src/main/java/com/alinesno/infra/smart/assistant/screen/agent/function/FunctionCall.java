package com.alinesno.infra.smart.assistant.screen.agent.function;

import java.util.Map;

public class FunctionCall {
    public static String invokeTool(String toolName , Map<String, Object> toolParams) {

        String content = """
               今天的产品动态：新增功能与优化
                新增功能
                领导角色管理视图组件（leaderModel.vue）：引入了新的领导角色管理视图组件，提供角色显示、任务管理和场景保存功能，使用户能够更方便地管理和查看领导角色及其关联任务。
                领导者场景支持：增加了对领导者场景的支持，并在 package-info.java 文件中进行了相应的描述更新，增强了系统的应用场景覆盖。
                角色类型选择功能：在角色管理视图中添加了角色类型选择功能，用户可以根据需要选择不同类型的领导角色，提升了灵活性。
                场景类型选择功能：新增场景类型选择功能，并完善了相关表单验证，确保数据输入的准确性。
                领导聊天视图组件：引入了新的聊天窗口及相关功能，实现了领导角色之间的即时通讯。
                领导任务完成监听器：新增了任务完成监听器类，用于处理任务完成事件并重新分配未完成的任务，确保任务流程的顺畅。
                任务分配事件类（TaskAssignedEvent）：新增任务分配事件类，包含任务属性及构造方法，便于系统追踪任务分配情况。
                任务完成事件类（TaskCompletionEvent）：新增任务完成事件类，包含任务ID及完成状态属性，方便系统监控任务状态。
                功能优化
                屏幕卡片标签显示和跳转逻辑：改进了屏幕卡片标签的显示效果和跳转逻辑，提升了用户体验。
                IndustryRoleCatalogDto 类优化：优化了 IndustryRoleCatalogDto 类，增加了注释并调整了字段命名规范，提高了代码的可读性和维护性。
                channelHome.vue 代码优化：在 channelHome.vue 中添加了空行，提高了代码的可读性。
                ScreenEntity 类修改：修改 screen_type 字段类型为 String 并添加了相关字段，以支持更多场景细节。
                ScreenDto 类扩展：在 ScreenDto 中添加了场景类型及管理人员和工作人员字段，丰富了数据结构。
                IndustryRoleEntity 类优化：在 IndustryRoleEntity 类中添加了角色类型字段及相关注解，增强了角色管理功能。
                RoleExecuteEntity 实体类：新增 RoleExecuteEntity 实体类，用于存储领导者场景执行结果，完善了数据模型。
                RoleExecuteMapper 接口：新增 RoleExecuteMapper 接口，用于角色执行实体的数据库操作，提升了数据持久化能力。
                IRoleExecuteService 接口：新增 IRoleExecuteService 接口，定义了角色执行服务的基础操作，明确了服务层职责。
                WorkerTaskListener 类：新增 WorkerTaskListener 类，用于监听并处理任务分配事件，提升了系统的事件响应能力。
                """ ;

        return content;
    }

    public static String invokeTool2(String toolName , Map<String, Object> toolParams) {

        String content = """
               本周的主要产品动态：新增功能与优化
                新增功能
                领导角色管理视图组件（leaderModel.vue）：引入了新的领导角色管理视图组件，提供角色显示、任务管理和场景保存功能，使用户能够更方便地管理和查看领导角色及其关联任务。
                领导者场景支持：增加了对领导者场景的支持，并在 package-info.java 文件中进行了相应的描述更新，增强了系统的应用场景覆盖。
                角色类型选择功能：在角色管理视图中添加了角色类型选择功能，用户可以根据需要选择不同类型的领导角色，提升了灵活性。
                场景类型选择功能：新增场景类型选择功能，并完善了相关表单验证，确保数据输入的准确性。
                领导聊天视图组件：引入了新的聊天窗口及相关功能，实现了领导角色之间的即时通讯。
                领导任务完成监听器：新增了任务完成监听器类，用于处理任务完成事件并重新分配未完成的任务，确保任务流程的顺畅。
                任务分配事件类（TaskAssignedEvent）：新增任务分配事件类，包含任务属性及构造方法，便于系统追踪任务分配情况。
                任务完成事件类（TaskCompletionEvent）：新增任务完成事件类，包含任务ID及完成状态属性，方便系统监控任务状态。
                功能优化
                屏幕卡片标签显示和跳转逻辑：改进了屏幕卡片标签的显示效果和跳转逻辑，提升了用户体验。
                IndustryRoleCatalogDto 类优化：优化了 IndustryRoleCatalogDto 类，增加了注释并调整了字段命名规范，提高了代码的可读性和维护性。
                channelHome.vue 代码优化：在 channelHome.vue 中添加了空行，提高了代码的可读性。
                ScreenEntity 类修改：修改 screen_type 字段类型为 String 并添加了相关字段，以支持更多场景细节。
                ScreenDto 类扩展：在 ScreenDto 中添加了场景类型及管理人员和工作人员字段，丰富了数据结构。
                IndustryRoleEntity 类优化：在 IndustryRoleEntity 类中添加了角色类型字段及相关注解，增强了角色管理功能。
                RoleExecuteEntity 实体类：新增 RoleExecuteEntity 实体类，用于存储领导者场景执行结果，完善了数据模型。
                RoleExecuteMapper 接口：新增 RoleExecuteMapper 接口，用于角色执行实体的数据库操作，提升了数据持久化能力。
                IRoleExecuteService 接口：新增 IRoleExecuteService 接口，定义了角色执行服务的基础操作，明确了服务层职责。
                WorkerTaskListener 类：新增 WorkerTaskListener 类，用于监听并处理任务分配事件，提升了系统的事件响应能力。
                """ ;

        return content;
    }
}
