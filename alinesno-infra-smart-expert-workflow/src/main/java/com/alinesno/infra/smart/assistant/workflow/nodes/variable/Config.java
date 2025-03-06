package com.alinesno.infra.smart.assistant.workflow.nodes.variable;

import lombok.Data;
import java.util.List;

@Data
public class Config {
    // 字段列表
    private List<ConfigField> fields;
}