package com.alinesno.infra.smart.assistant.role.yaml;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TableDefinitionBean {

    private Map<String, List<TableDetail>> tables;

    @Data
    public static class TableDetail {
        private String name;
        private String desc;
    }
}
