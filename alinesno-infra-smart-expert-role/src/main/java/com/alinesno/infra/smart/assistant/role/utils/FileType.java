package com.alinesno.infra.smart.assistant.role.utils;

public enum FileType {
    JAVA("java", "Java"),
    PYTHON("python", "Python"),
    YAML("yaml", "YAML"),
    MARKDOWN("markdown", "Markdown"),
    SQL("sql", "SQL"),
    UNKNOWN("unknown", "Unknown");

    private final String value;
    private final String label;

    FileType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public boolean isUnknown() {
        return value.equals("unknown");
    }
}
