package com.alinesno.infra.smart.assistant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 执行角色类型枚举类
 */
@Getter
@AllArgsConstructor
public enum ExecuteRoleTypeEnums {

    // 单角色：可以单独运行的角色，不用接收上下文信息
    SINGLE_ROLE("single_role", "单角色", "可以单独运行的角色，不用接收上下文信息"),

    // 协作角色： 需要同时会带有上一个节点任务的信息，可以带有多个节点的任务内容
    COLLABORATIVE_ROLE("collaborative_role", "协作角色", "需要同时会带有上一个节点任务的信息，可以带有多个节点的任务内容"),

    // 目录设计： 生成大文编辑目录角色，用于文本编辑目录
    CATALOG_DESIGN("catalog_design", "目录设计", "生成大文编辑目录角色，用于文本编辑目录"),

    // 文本编辑： 针对于文本内容编辑角色，用于文本内容编辑
    TEXT_EDITING("text_editing", "文本编辑", "针对于文本内容编辑角色，用于文本内容编辑"),

    // 管理者： 安排协调其它人员的角色，用于安排其它角色任务及工作
    MANAGER("manager", "管理者", "安排协调其它人员的角色，用于安排其它角色任务及工作");

    private final String code;
    private final String name;
    private final String desc;

}