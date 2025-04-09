package com.alinesno.infra.smart.im.enums;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 通道类型枚举
 * 定义了不同类型的频道及其特性
 */
@Getter
public enum ChannelType {

    /**
     * 组织频道
     * 组织频道可以选择公共和组织内的智能体拉入频道
     */
    ORG_CHANNEL("org", "组织频道", "组织频道可以选择公共和组织内的智能体加入频道", "fa-solid fa-truck-plane"),

    /**
     * 私有频道
     * 组织频道可以选择公共和组织内的智能体拉入频道，频道只为个人所见
     */
    PRIVATE_CHANNEL("private", "私有频道", "组织频道可以选择公共和组织内的智能体加入频道，频道只为个人所见", "fa-solid fa-lock"),

    /**
     * 公开频道
     * 公开频道只能选择公开的智能体拉入频道
     */
    PUBLIC_CHANNEL("public", "公开频道", "公共频道只能选择公开的智能体加入频道", "fa-solid fa-globe");

    // 通道类型的值
    private final String value;
    // 通道类型的标签
    private final String label;
    // 通道类型的描述
    private final String desc;
    // 通道类型的图标
    private final String icon;

    /**
     * 构造一个通道类型
     *
     * @param value 通道类型的值
     * @param label 通道类型的标签
     * @param desc  通道类型的描述
     * @param icon  通道类型的图标
     */
    ChannelType(String value, String label, String desc, String icon) {
        this.value = value;
        this.label = label;
        this.desc = desc;
        this.icon = icon;
    }

    /**
     * 获取到所有频道信息
     */
    public static List<Map<String, String>> getAllChannelTypes() {
        return Stream.of(values())
                .map(channelType -> Map.of(
                        "value", channelType.getValue(),
                        "label", channelType.getLabel(),
                        "desc", channelType.getDesc(),
                        "icon", channelType.getIcon()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 通过value获取到对应的枚举
     */
    public static ChannelType getByValue(String value) {
        for (ChannelType channelType : values()) {
            if (channelType.getValue().equals(value)) {
                return channelType;
            }
        }
        return null;
    }

    /**
     * 判断是否是合法的频道类型
     * @param channelType
     * @return
     */
    public static boolean isValidChannelType(String channelType) {
        return getByValue(channelType) != null;
    }
}