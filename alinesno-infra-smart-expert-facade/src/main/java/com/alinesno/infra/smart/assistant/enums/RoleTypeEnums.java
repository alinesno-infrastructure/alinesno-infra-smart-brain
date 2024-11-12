package com.alinesno.infra.smart.assistant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色类型枚举
 */
@AllArgsConstructor
@Getter
public enum RoleTypeEnums {

    SINGLE_ROLE("single_role", "单角色", "自己单独完成一个聊天，可流式输出或者同步输出"),
    COLLABORATIVE_ROLE("collaborative_role", "协作角色", "与其它角色协作才可以完成一个工作，可流式输出或者同步输出"),
    SCENARIO_ROLE("scenario_role", "场景角色", "与其它角色协作才可以完成一个工作，只能同步输出"),
    COMBINED_ROLE("combined_role", "组合角色", "结合了协作与场景角色的特点，既可以在协作中发挥作用，也可以适应特定场景，支持流式输出和同步输出");;

    private final String key;
    private final String name;
    private final String description;

    /**
     * 通过key获取到角色信息
     */
    public static RoleTypeEnums getRoleTypeByKey(String key) {
        for (RoleTypeEnums roleType : RoleTypeEnums.values()) {
            if (roleType.getKey().equals(key)) {
                return roleType;
            }
        }
        return null;
    }
}