package com.alinesno.infra.base.search.service;

import java.io.File;
import java.util.List;

/**
 * 文档解析服务接口，定义了对不同类型文档的解析方法
 */
public interface IDocumentParserService {

    /**
     * 解析文档内容并将其分割为指定长度的段落
     *
     * @param documentContent   待分割的文档内容
     * @param maxSegmentLength  每个段落的最大长度
     * @return 分割后的段落列表
     */
    List<String> documentParser(String documentContent , int maxSegmentLength) ;

    /**
     * 解析PDF文档并返回文本内容列表
     */
    List<String> parsePDF(File file);

    /**
     * 获取DOCX文档的内容列表
     */
    List<String> getContentDocx(File file);

    /**
     * 将XMind文件转换为文本内容列表
     */
    List<String> xmindToList(File file);

    /**
     * 获取DOC文件的内容列表
     */
    List<String> getContentDoc(File file);

    /**
     * 解析Markdown文件并返回文本内容列表
     */
    List<String> parseMD(File file);

    /**
     * 解析Excel文件并返回文本内容列表
     */
    List<String> parseExcel(File file);

    /**
     * 解析txt文本
     * @param targetFile
     * @return
     */
    List<String> parseTxt(File targetFile);
}
