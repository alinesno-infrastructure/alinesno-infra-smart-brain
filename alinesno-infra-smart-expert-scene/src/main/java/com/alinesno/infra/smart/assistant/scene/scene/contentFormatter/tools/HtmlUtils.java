package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlUtils {
    
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
    }