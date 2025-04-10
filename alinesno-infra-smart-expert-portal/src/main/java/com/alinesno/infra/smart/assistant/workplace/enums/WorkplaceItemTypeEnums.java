package com.alinesno.infra.smart.assistant.workplace.enums;

import lombok.Getter;

/**
 * 该枚举类定义了工作台元素的类型，包括智能体、频道和场景，
 * 同时包含每个类型对应的 code 和 label。
 */
@Getter
public enum WorkplaceItemTypeEnums {

    AGENT("agent", "智能体"),
    CHANNEL("channel", "频道"),
    SCENE("scene", "场景");

    /**
     * 类型的 code
     */
    private final String code;

    /**
     * 类型的 label
     */
    private final String label;

    /**
     * 枚举类的构造方法
     *
     * @param code 类型的 code
     * @param label 类型的 label
     */
    WorkplaceItemTypeEnums(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 判断给定的工作台类型是否是有效的
     * @param workplaceType
     * @return
     */
    public static boolean isValidate(String workplaceType) {
        for (WorkplaceItemTypeEnums value : values()) {
            if (value.getCode().equals(workplaceType)) {
                return true;
            }
        }
        return false;
    }

    public static WorkplaceItemTypeEnums getByCode(String type) {
        for (WorkplaceItemTypeEnums value : values()) {
            if (value.getCode().equals(type)) {
                return value;
            }
        }
        return null;
    }
}