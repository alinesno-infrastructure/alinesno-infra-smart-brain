package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

/**
 * Docx Format HTML内容格式化的处理
 */
public class DocxHtmlFormatterUtils {

    /**
     * 确保HTML内容有html和body标签
     * @param htmlContent
     * @return
     */
    public static String ensureHtmlStructure(String htmlContent) {
        // 使用Jsoup解析HTML内容
        Document doc = Jsoup.parse(htmlContent);

        // 检查是否有html和body标签
        boolean hasHtmlTag = doc.getElementsByTag("html").isEmpty();
        boolean hasBodyTag = doc.getElementsByTag("body").isEmpty();

        // 如果缺少html或body标签，则重新构建文档
        if (!hasHtmlTag || !hasBodyTag) {
            Document newDoc = Document.createShell("");

            // 添加head中的必要meta标签
            Element head = newDoc.head();
            head.appendElement("meta")
                    .attr("http-equiv", "Content-Type")
                    .attr("content", "text/html; charset=utf-8");
            head.appendElement("meta")
                    .attr("http-equiv", "Content-Style-Type")
                    .attr("content", "text/css");

            // 将原始内容放入body中的div里
            Element body = newDoc.body();
            Element div = body.appendElement("div");
            div.html(htmlContent);

            return newDoc.outerHtml();
        }

        // 如果已有完整结构，则返回原内容
        return htmlContent;
    }

    /**
     * 添加红头文件内容到Body之后
     * @param htmlContent 原始HTML内容
     * @param headerHtml 红头文件HTML内容
     * @return 添加红头后的HTML内容
     */
    public static String addHeaderToBody(String htmlContent, String headerHtml) {

        htmlContent = ensureHtmlStructure(htmlContent) ;

        Document doc = Jsoup.parse(htmlContent);
        Element body = doc.body();

        // 在body的最前面插入红头内容
        body.prepend(headerHtml);

        return doc.outerHtml();
    }

    /**
     * 添加底部内容到Body之后
     * @param htmlContent 原始HTML内容
     * @param footerHtml 底部HTML内容
     * @return 添加底部内容后的HTML内容
     */
    public static String addFooterToBody(String htmlContent, String footerHtml) {

        htmlContent = ensureHtmlStructure(htmlContent) ;

        Document doc = Jsoup.parse(htmlContent);
        Element body = doc.body();

        // 在body的最后面插入底部内容
        body.append(footerHtml);

        return doc.outerHtml();
    }

    /**
     * 判断HTML内容是否为空
     * @param headerHtml
     * @return
     */
    public static boolean hasText(String headerHtml) {
        return headerHtml != null && !headerHtml.trim().isEmpty() ;
    }

    /**
     * HTML内容转纯文本，并截取指定字段长度
     * @param documentContent HTML内容
     * @param maxLength 最大文本长度
     * @return 纯文本内容，截取到指定长度
     */
    public static String htmlToText(String documentContent, int maxLength) {
        if (documentContent == null || documentContent.trim().isEmpty()) {
            return "";
        }

        // Parse HTML and get text content
        Document doc = Jsoup.parse(documentContent);
        String text = doc.text().trim();

        // Truncate if necessary
        if (maxLength > 0 && text.length() > maxLength) {
            text = text.substring(0, maxLength) + "...";
        }

        return text;
    }
}