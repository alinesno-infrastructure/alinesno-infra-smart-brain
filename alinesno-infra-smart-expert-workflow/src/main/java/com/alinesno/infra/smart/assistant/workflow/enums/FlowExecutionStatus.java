package com.alinesno.infra.smart.assistant.workflow.enums;

import lombok.Getter;

/**
 * 该枚举类用于表示流程执行的状态。
 * 它包含了三种常见的流程执行状态：执行中、完成和异常，并且为每个状态提供了对应的代码和标签。
 * 代码（code）是小写英文，用于以简洁的文本形式表示状态，方便在数据传输或存储中使用。
 * 标签（label）是中文描述，用于在界面显示或日志记录中提供更友好的信息。
 */
@Getter
public enum FlowExecutionStatus {

    /**
     * 表示流程正在执行的状态。
     * 对应的代码为 "executing"，标签为 "执行中"。
     */
    EXECUTING("executing", "执行中"),

    /**
     * 表示流程已经成功完成的状态。
     * 对应的代码为 "completed"，标签为 "完成"。
     */
    COMPLETED("completed", "完成"),

    /**
     * 表示流程执行过程中出现异常的状态。
     * 对应的代码为 "error"，标签为 "异常"。
     */
    ERROR("error", "异常");

    /**
     * 状态的代码，为小写英文，用于简洁表示状态。
     * -- GETTER --
     *  获取状态的代码。
     *
     * @return 状态的代码，为小写英文。

     */
    private final String code;

    /**
     * 状态的标签，为中文描述，用于友好显示状态信息。
     * -- GETTER --
     *  获取状态的标签。
     *
     * @return 状态的标签，为中文描述。

     */
    private final String label;

    /**
     * 枚举的构造函数，用于初始化每个枚举实例的代码和标签。
     *
     * @param code  状态的代码，应为小写英文。
     * @param label 状态的标签，应为中文描述。
     */
    FlowExecutionStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 根据代码查找对应的枚举实例。
     *
     * @param code 要查找的状态代码，应为小写英文。
     * @return 与给定代码对应的枚举实例，如果未找到则抛出 IllegalArgumentException 异常。
     * @throws IllegalArgumentException 如果未找到与给定代码匹配的枚举实例。
     */
    public static FlowExecutionStatus getByCode(String code) {
        for (FlowExecutionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未找到对应的 FlowExecutionStatus 枚举，code: " + code);
    }
}