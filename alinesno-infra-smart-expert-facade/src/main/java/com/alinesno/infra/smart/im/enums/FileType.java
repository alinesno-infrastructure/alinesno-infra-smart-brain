package com.alinesno.infra.smart.im.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.text.html.HTML;

/**
 * 文件类型枚举
 */
@Getter
@AllArgsConstructor
public enum FileType {
    PDF("pdf", "PDF文件"),
    DOCX("docx", "Word文档"),
    XLSX("xlsx", "Excel文档"),
    PPT("ppt", "PowerPoint文档"),
    IMAGE("image", "图片"),
    TXT("txt", "文本文件"),
    ZIP("zip", "压缩文件"),
    CSV("csv", "CSV文件"),
    HTML("html", "HTML文件"),
    MESSAGE("message", "消息"),
    LINK("link", "链接"),
    URL("url", "URL"),
    FILE("file", "文件");

    private final String code;
    private final String desc;

    /**
     * 根据文件名推断文件类型
     * @param fileName 文件名
     * @return 推断的文件类型
     */
    public static FileType inferFromFileName(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return FILE;
        }

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> PDF;
            case "docx", "doc" -> DOCX;
            case "xlsx", "xls" -> XLSX;
            case "ppt", "pptx" -> PPT;
            case "png", "jpg", "jpeg", "gif", "bmp", "svg", "webp", "tiff" -> IMAGE;
            case "txt", "text", "log" -> TXT;
            case "zip", "rar", "7z", "tar", "gz" -> ZIP;
            case "csv" -> CSV;
            case "html", "htm" -> HTML;
            default -> FILE;
        };
    }
}