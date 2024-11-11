package com.alinesno.infra.smart.assistant.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 行业角色目录传输对象
 * 用于在不同层之间传递行业角色及其相关信息
 */
@Data
public class IndustryRoleCatalogDto {

    /**
     * 目录项的唯一标识符
     */
    private Long id;

    /**
     * 角色图标的路径或名称
     */
    private String icon;

    /**
     * 角色目录的名称
     */
    private String name;

    /**
     * 角色在目录中的排序编号
     */
    private int orderNum;

    /**
     * 角色的描述信息
     */
    private String description;

    /**
     * 该目录下的行业角色列表
     * 初始化为一个空的ArrayList，以便于在创建实例时直接使用
     */
    private List<IndustryRoleDto> agents = new ArrayList<>();

}
