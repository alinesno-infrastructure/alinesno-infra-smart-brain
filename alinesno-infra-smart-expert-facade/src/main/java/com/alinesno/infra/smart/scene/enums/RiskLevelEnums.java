package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

/**
 * 风险级别枚举
 */
@Getter
public enum RiskLevelEnums {

    LOW("低", "low", "低风险"),
    MEDIUM("中", "medium", "中风险"),
    HIGH("高", "high", "高风险");

    private final String label;
    private final String value;
    private final String desc;

    RiskLevelEnums(String label, String value, String desc) {
        this.label = label;
        this.value = value;
        this.desc = desc;
    }

    /**
     * 判断value是否在枚举中
     */
    public static boolean contains(String value) {
        for (RiskLevelEnums level : RiskLevelEnums.values()) {
            if (level.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
