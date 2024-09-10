package com.alinesno.infra.smart.im.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 支持的文档类型
 */
@Getter
public enum FileTypeEnum {

    AEP("Adobe Illustrator 文件", "aep", "aep.png"),
    AI("Adobe Illustrator 图像", "ai", "ai.png"),
    AVI("音频视频交错格式", "avi", "avi.png"),
    MP4("音频视频交错格式", "mp4", "avi.png"),
    CDR("CorelDRAW 图形文件", "cdr", "cdr.png"),
    CSS("层叠样式表", "css", "css.png"),
    DOC("Microsoft Word 文档", "doc", "doc.png"),
    EPS("Encapsulated PostScript", "eps", "eps.png"),
    GIF("图形交换格式", "gif", "gif.png"),
    HTML("超文本标记语言", "html", "html.png"),
    JPEG("JPEG 图像", "jpeg", "jpeg.png"),
    JPG("JPG 图像", "jpg", "jpeg.png"),
    WEBP("WEBP 图像", "webp", "jpeg.png"),
    MOV("QuickTime 视频", "mov", "mov.png"),
    MP3("压缩音频格式", "mp3", "mp3.png"),
    WAV("压缩音频格式", "wav", "mp3.png"),
    NEW("新文件", "new", "new.png"), // 这个可能是特殊用途，通常不会有这种扩展名
    PDF("便携式文档格式", "pdf", "pdf.png"),
    PHP("PHP 脚本", "php", "php.png"),
    PNG("可移植网络图形", "png", "png.png"),
    PPT("Microsoft PowerPoint 演示文稿", "ppt", "ppt.png"),
    PSD("Photoshop 图像文件", "psd", "psd.png"),
    RAR("RAR 压缩包", "rar", "rar.png"),
    TTF("TrueType 字体", "ttf", "ttf.png"),
    TXT("纯文本文件", "txt", "txt.png"),
    URL("互联网快捷方式", "url", "url.png"),
    XLS("Microsoft Excel 工作簿", "xls", "xls.png"),
    ZIP("ZIP 压缩包", "zip", "zip.png");

    private final String name;
    private final String desc;
    private final String icon;

    FileTypeEnum(String desc , String name, String icon) {
        this.desc = desc;
        this.name = name;
        this.icon = icon;
    }

    // 通过name获取到icon
    public static String getIconByName(String name) {
        for (FileTypeEnum type : FileTypeEnum.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type.getIcon();
            }
        }
        return null;
    }

    public static List<FileTypeEnum> getAllFileTypes() {
        return Arrays.asList(FileTypeEnum.values());
    }

    public static Object[] getAllNames() {
        FileTypeEnum[] FileTypes = FileTypeEnum.values();
        Object[] names = new Object[FileTypes.length];
        for (int i = 0; i < FileTypes.length; i++) {
            names[i] = FileTypes[i].getName();
        }
        return names;
    }

    public static String getAllNameStr() {

        StringBuilder names = new StringBuilder();

        for(FileTypeEnum d : getAllFileTypes()){
            names.append(",")
                 .append(d.getName());
        }

        return names.toString();
    }
}
