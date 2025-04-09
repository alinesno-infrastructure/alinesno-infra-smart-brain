package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 场景类型枚举
 * 定义了不同类型的场景及其特性
 */
@Getter
public enum SceneScopeType {

    /**
     * 组织场景
     * 组织场景可以选择公共和组织内的智能体加入场景
     */
    ORG_SCENE("org", "组织场景", "组织场景可以选择公共和组织内的智能体加入场景", "fa-solid fa-truck-plane"),

    /**
     * 私有场景
     * 私有场景可以选择公共和组织内的智能体加入场景，场景只为个人所见
     */
    PRIVATE_SCENE("private", "私有场景", "私有场景可以选择公共和组织内的智能体加入场景，场景只为个人所见", "fa-solid fa-lock"),

    /**
     * 公开场景
     * 公开场景只能选择公开的智能体加入场景
     */
    PUBLIC_SCENE("public", "公开场景", "公开场景只能选择公开的智能体加入场景", "fa-solid fa-globe");

    // 场景类型的值
    private final String value;
    // 场景类型的标签
    private final String label;
    // 场景类型的描述
    private final String desc;
    // 场景类型的图标
    private final String icon;

    /**
     * 构造一个场景类型
     *
     * @param value 场景类型的值
     * @param label 场景类型的标签
     * @param desc  场景类型的描述
     * @param icon  场景类型的图标
     */
    SceneScopeType(String value, String label, String desc, String icon) {
        this.value = value;
        this.label = label;
        this.desc = desc;
        this.icon = icon;
    }

    /**
     * 获取到所有场景信息
     */
    public static List<Map<String, String>> getAllSceneTypes() {
        return Stream.of(values())
               .map(sceneType -> Map.of(
                        "value", sceneType.getValue(),
                        "label", sceneType.getLabel(),
                        "desc", sceneType.getDesc(),
                        "icon", sceneType.getIcon()
                ))
               .collect(Collectors.toList());
    }

    /**
     * 通过value获取到对应的枚举
     */
    public static SceneScopeType getByValue(String value) {
        for (SceneScopeType sceneType : values()) {
            if (sceneType.getValue().equals(value)) {
                return sceneType;
            }
        }
        return null;
    }

}    