package com.alinesno.infra.smart.assistant.scene.scene.documentReview.enums;

import lombok.Getter;

/**
 * 文档生成状态枚举类
 */
@Getter
public enum ReviewRuleGenStatusEnums {

    /**
     * 未生成 - 尚未生成审核清单
     */
    NOT_GENERATED("not_generated", "未生成"),

    /**
     * 生成中 - 审核清单正在生成中
     */
    GENERATING("generating", "生成中"),

    /**
     * 生成成功 - 审核清单生成完成且内容有效
     */
    SUCCESS("success", "生成成功"),

    /**
     * 生成失败(无内容) - 审核清单生成失败，返回内容为空
     */
    FAILED_NO_CONTENT("failed_no_content", "生成失败(无内容)"),

    /**
     * 生成失败 - 审核清单生成过程中出现错误
     */
    FAILED("failed", "生成失败"),

    /**
     * 生成超时 - 审核清单生成超时
     */
    TIMEOUT("timeout", "生成超时");

    private final String code;
    private final String label;

    ReviewRuleGenStatusEnums(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 根据code获取枚举
     */
    public static ReviewRuleGenStatusEnums getByCode(String code) {
        for (ReviewRuleGenStatusEnums status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据label获取枚举
     */
    public static ReviewRuleGenStatusEnums getByLabel(String label) {
        for (ReviewRuleGenStatusEnums status : values()) {
            if (status.getLabel().equals(label)) {
                return status;
            }
        }
        return null;
    }
}