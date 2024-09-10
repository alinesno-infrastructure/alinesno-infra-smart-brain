package com.alinesno.infra.smart.im.enums;

import lombok.Getter;

/**
 * 支持的文档类型
 */
@Getter
public enum DocumentTypeEnum {

    PPT("PPT", "支持PPT、PPTX等，兼容WPS、Office文档类型", "fa-solid fa-file-powerpoint", new String[]{"pptx" , "ppt"}),
    EXCEL("Excel", "支持Excel等，兼容WPS、Office文档类型", "fa-solid fa-file-excel", new String[]{"xlsx"}),
    WORD("Word", "支持Doc、Docx等，兼容WPS、Office文档类型", "fa-solid fa-file-word", new String[]{"docx" , "doc"}),
    CVS("CVS", "支持CVS在线查看等，兼容WPS、Office文档类型", "fa-solid fa-file-csv", new String[]{"cvs"}), // 假设CSV没有直接对应
    PDF("PDF", "支持PDF在线查看等，兼容WPS、Office文档类型", "fa-solid fa-file-pdf", new String[]{"pdf"});

    private final String name;
    private final String desc;
    private final String icon;
    private final String[] fileTypes;

    DocumentTypeEnum(String name, String desc, String icon, String[] fileTypes) {
        this.name = name;
        this.desc = desc;
        this.icon = icon;
        this.fileTypes = fileTypes;
    }

    // 通过传递类型获取到icon，通过fileTypes判断是否包含在这个类型中
    public static String getIconByFileType(String fileType) {
        for (DocumentTypeEnum type : DocumentTypeEnum.values()) {
            for (String typeFileType : type.fileTypes) {
                if (typeFileType.equalsIgnoreCase(fileType)) {
                    return type.icon;
                }
            }
        }
        return DocumentTypeEnum.WORD.getIcon() ;
    }

}
