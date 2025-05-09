package com.alinesno.infra.smart.deepsearch.enums;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 步骤动作状态枚举
 */
@Getter
public enum StepActionStatusEnums {

    UNDO("undo", "未进行"),
    DONE("done", "成功"),
    FAIL("fail", "失败"),
    DOING("doing", "进行中");

    private final String key;
    private final String label;

    StepActionStatusEnums(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public static StepActionStatusEnums getByKey(String key) {
        for (StepActionStatusEnums value : StepActionStatusEnums.values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }
}