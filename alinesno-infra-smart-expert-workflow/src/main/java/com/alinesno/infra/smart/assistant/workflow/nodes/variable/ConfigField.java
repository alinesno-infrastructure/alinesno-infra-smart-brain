package com.alinesno.infra.smart.assistant.workflow.nodes.variable;

import lombok.Data;

@Data
public class ConfigField {
    // 字段标签
    private String label;
    // 字段值
    private String value;
}