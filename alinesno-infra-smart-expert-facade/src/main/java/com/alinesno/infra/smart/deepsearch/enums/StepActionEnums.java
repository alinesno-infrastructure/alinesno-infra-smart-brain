package com.alinesno.infra.smart.deepsearch.enums;

import lombok.Getter;

/**
 * 步骤动作枚举
 */
@Getter
public enum StepActionEnums {

    // 生成HTML界面
    GENERATE_HTML("generateHtml", "生成HTML界面", "fa-solid fa-file-code"),

    ANALYSIS("analysis", "问题分析", "fa-solid fa-magnifying-glass-chart"),
    PLAN("plan", "执行规划", "fa-solid fa-calendar-check"),
    CHAT("chat", "AI对话聊天", "fa-solid fa-comments"),  // 聊天
    THINK("think", "思考当中", "fa-solid fa-lightbulb"),
    TOOL("tool", "使用工具", "fa-solid fa-screwdriver-wrench"),
    SUMMARY("summary", "内容总结", "fa-solid fa-file-lines");

    private final String actionType;
    private final String description;
    private final String fontAwesomeIcon;

    StepActionEnums(String actionType, String description, String fontAwesomeIcon) {
        this.actionType = actionType;
        this.description = description;
        this.fontAwesomeIcon = fontAwesomeIcon;
    }

    /**
     * 根据动作类型获取对应的枚举值
     * @param actionType
     * @return
     */
    public static StepActionEnums getByActionType(String actionType) {
        for (StepActionEnums value : values()) {
            if (value.actionType.equals(actionType)) {
                return value;
            }
        }
        return CHAT;
    }
}