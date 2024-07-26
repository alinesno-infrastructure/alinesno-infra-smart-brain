package com.alinesno.infra.smart.assistant.role.yaml;

import lombok.Data;

import java.util.List;

@Data
public class TableDDLDefinitionBean {
    // 表名
    private String tableName;

    // 列定义列表
    private List<ColumnDefinition> columns;

    // 主键定义
    private String primaryKey;

    // 外键定义
    private List<ForeignKey> foreignKeys ;

    // 存储引擎
    private String engine;

    // 表注释
    private String comment;

    @Data
    public static class ForeignKey {
        private String name; // 外键名称
        private String columns; // 外键关联的字段
        private String referenceTable; // 外键关联的表
        private String referenceColumns; // 外键关联的字段
    }

    @Data
    public static class ColumnDefinition {
        // 列名
        private String name;

        // 列类型
        private String type;

        // 是否自增
        private boolean autoIncrement;

        // 是否允许为空
        private boolean isNull;

        // 列注释
        private String comment;

        // 默认值
        private String defaultVal;
    }
}
