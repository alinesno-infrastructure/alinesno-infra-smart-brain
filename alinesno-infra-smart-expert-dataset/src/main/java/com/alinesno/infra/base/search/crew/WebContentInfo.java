package com.alinesno.infra.base.search.crew;

import lombok.Data;

/**
 * 该类用于存储解析后的网页或文件的相关信息。
 */
@Data
public class WebContentInfo {
    // 网页或文件的链接地址
    private String url;
    // 网页或文件的标题
    private String title;
    // 网页或文件的内容
    private String content;
    // 网页或文件的类型，如 html、pdf、docx 等
    private String type;
}