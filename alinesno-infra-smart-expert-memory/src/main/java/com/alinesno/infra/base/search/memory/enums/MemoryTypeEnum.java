package com.alinesno.infra.base.search.memory.enums;

import lombok.Getter;

@Getter
public enum MemoryTypeEnum {
    /**
     * 表示基于对话的记忆。
     */
    CONVERSATION("conversation", "对话记忆"),

    /**
     * 表示观察性记忆。
     */
    OBSERVATION("observation", "观察记忆"),

    /**
     * 表示通过分析得出的洞见性记忆。
     */
    INSIGHT("insight", "洞见记忆"),

    /**
     * 表示长期记忆，例如知识库、数据库等。
     */
    LONG_TERM("longterm", "长期记忆"),

    /**
     * 感知记忆: 从外部世界获取的即时信息，如视觉、听觉输入，帮助智能体理解当前环境。（比如天气或者工作计划）
     */
    PERCEPTUAL("perceptual", "感知记忆") ;

    // 关联的字符串值
    private final String type;

    /**
     * -- GETTER --
     *  获取中文标签。
     *
     * @return 中文标签
     */
    // 中文标签
    private final String label;

    // 构造函数，用于初始化枚举成员的关联字符串值和中文标签
    MemoryTypeEnum(String type, String label) {
        this.type = type;
        this.label = label;
    }

    // 重写toString方法，使得返回的是关联的字符串值，而不是默认的枚举名称
    @Override
    public String toString() {
        return this.type;
    }

    /**
     * 根据字符串值获取对应的枚举成员。
     *
     * @param type 字符串表示的记忆类型
     * @return 对应的枚举成员，如果找不到匹配项则抛出IllegalArgumentException
     */
    public static MemoryTypeEnum fromString(String type) {
        for (MemoryTypeEnum memoryType : MemoryTypeEnum.values()) {
            if (memoryType.toString().equalsIgnoreCase(type)) {
                return memoryType;
            }
        }
        throw new IllegalArgumentException("未知的记忆类型: " + type);
    }
}