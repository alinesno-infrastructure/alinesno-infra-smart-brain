package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

/**
 * 审核立场枚举
 */
@Getter
public enum ReviewPositionEnums {

    // 甲方、乙方、中立
    PARTY_A("party_a", "甲方"),
    PARTY_B("party_b", "乙方"),
    NEUTRAL("neutral", "中立");

    private final String value;
    private final String label;

    ReviewPositionEnums(String value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ReviewPositionEnums getByValue(String value) {
        for (ReviewPositionEnums enums : ReviewPositionEnums.values()) {
            if (enums.getValue().equals(value)) {
                return enums;
            }
        }
        return null;
    }

    /**
     * 判断立场是否在枚举中
     */
    public static boolean contains(String value) {
        for (ReviewPositionEnums enums : ReviewPositionEnums.values()) {
            if (enums.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

}