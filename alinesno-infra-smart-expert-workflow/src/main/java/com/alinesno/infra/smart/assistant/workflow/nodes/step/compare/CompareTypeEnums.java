package com.alinesno.infra.smart.assistant.workflow.nodes.step.compare;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 比较类型枚举
 * 定义了所有支持的比较操作类型
 */
@Getter
@AllArgsConstructor
public enum CompareTypeEnums {

    IS_NULL("is_null", "为空"),
    IS_NOT_NULL("is_not_null", "不为空"),
    CONTAIN("contain", "包含"),
    NOT_CONTAIN("not_contain", "不包含"),
    EQ("eq", "等于"),
    GE("ge", "大于等于"),
    GT("gt", "大于"),
    LE("le", "小于等于"),
    LT("lt", "小于"),
    LEN_EQ("len_eq", "长度等于"),
    LEN_GE("len_ge", "长度大于等于"),
    LEN_GT("len_gt", "长度大于"),
    LEN_LE("len_le", "长度小于等于"),
    LEN_LT("len_lt", "长度小于");

    /**
     * 比较类型值
     */
    private final String value;
    
    /**
     * 比较类型显示标签
     */
    private final String label;
}
