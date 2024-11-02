package com.alinesno.infra.smart.assistant.template.enums;

import lombok.Getter;

/**
 * 文件类型枚举类
 */
@Getter
public enum FileTypeEnums {

    TXT("txt", "文本文件"),
    PDF("pdf", "PDF文件"),
    XML("xml", "XML文件"),
    DOCX("docx", "Word文档"),
    DOC("doc", "旧版Word文档"),
    MD("md", "Markdown文件"),
    EXCEL("xlsx", "Excel文件");

    private final String value;
    private final String label;

    /**
     * 构造方法
     * @param value 文件类型值
     * @param label 文件类型标签
     */
    FileTypeEnums(String value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据文件类型值获取对应的枚举常量
     * @param value 文件类型值
     * @return 对应的枚举常量，如果不存在则返回null
     */
    public static FileTypeEnums getByValue(String value) {
        for (FileTypeEnums constant : FileTypeEnums.values()) {
            if (constant.value.equals(value)) {
                return constant;
            }
        }
        return null;
    }

}