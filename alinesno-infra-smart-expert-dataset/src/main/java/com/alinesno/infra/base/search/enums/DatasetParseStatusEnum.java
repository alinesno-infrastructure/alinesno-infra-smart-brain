package com.alinesno.infra.base.search.enums;

import lombok.Getter;

/**
 * 数据集解析状态的枚举类。
 * 该枚举类定义了数据集解析过程中可能出现的各种状态，
 * 每个状态包含一个代码（code）和一个对应的标签（label），
 * 代码用于在数据库或系统中标识状态，标签用于提供状态的中文描述，方便显示和理解。
 */
@Getter
public enum DatasetParseStatusEnum {
    /**
     * 数据集解析已开始的状态。
     * 当数据集解析任务开始执行时，对应的日志记录会标记为该状态。
     */
    STARTED("STARTED", "已开始"),
    /**
     * 数据集解析已完成的状态。
     * 当数据集解析任务成功完成时，对应的日志记录会更新为该状态。
     */
    COMPLETED("COMPLETED", "已完成"),
    /**
     * 数据集解析被中断的状态。
     * 当数据集解析任务在执行过程中被中断（如线程被中断）时，对应的日志记录会更新为该状态。
     */
    INTERRUPTED("INTERRUPTED", "被中断"),
    /**
     * 数据集解析失败的状态。
     * 当数据集解析任务在执行过程中出现异常导致失败时，对应的日志记录会更新为该状态。
     */
    FAILED("FAILED", "失败");

    private final String code;
    private final String label;

    /**
     * 枚举类的构造函数，用于初始化状态的代码和标签。
     *
     * @param code 状态的代码，用于在系统中唯一标识该状态
     * @param label 状态的中文标签，用于显示和理解状态信息
     */
    DatasetParseStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

}