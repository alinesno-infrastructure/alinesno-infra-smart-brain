package com.alinesno.infra.base.search.enums;

// 定义检索类型的枚举
import lombok.Getter;

@Getter
public enum SearchType {
    VECTOR("vector", "向量检索"),
    FULLTEXT("fulltext", "全文检索"),
    HYBRID("hybrid", "混合检索");

    private final String code;
    private final String label;

    SearchType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    SearchType() {
        // 如果没有显式传入 code 和 label，使用枚举名作为默认值
        this.name().toLowerCase();
        this.code = this.name().toLowerCase();
        this.label = this.name().toLowerCase();
    }
}