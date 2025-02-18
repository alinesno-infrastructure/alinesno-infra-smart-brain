package com.alinesno.infra.base.search.service.impl;

import com.alinesno.infra.base.search.service.IDocumentParserService;
import com.alinesno.infra.base.search.utils.parse.ExcelParser;
import com.alinesno.infra.base.search.utils.parse.MarkdownParser;
import com.alinesno.infra.base.search.utils.parse.PDFParser;
import com.alinesno.infra.base.search.utils.parse.WordParser;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * 文档解析服务实现类，实现了文档解析服务接口中定义的方法
 */
@Slf4j
@Component
public class DocumentParserServiceImpl implements IDocumentParserService {

    @Override
    public List<String> documentParser(String text, int maxLength) {

        if(text == null || text.isEmpty()){
            return Collections.emptyList();
        }

        Document document = new Document(text);

        DocumentSplitter splitter = DocumentSplitters.recursive(maxLength,10,new OpenAiTokenizer());
        List<TextSegment> segments = splitter.split(document);

        return segments.stream().map(TextSegment::text).toList();
    }

    /**
     * 解析PDF文档并返回文本内容列表
     */
    @Override
    public List<String> parsePDF(File file) {
        String text = PDFParser.parsePDF(file.getAbsolutePath()) ;
        return List.of(text);
    }

    /**
     * 获取DOCX文档的内容列表
     */
    @Override
    public List<String> getContentDocx(File file) {
        return WordParser.searchWordDocX(file.getAbsolutePath());
    }

    /**
     * 将XMind文件转换为文本内容列表
     */
    @Override
    public List<String> xmindToList(File file) {
        return null;
    }

    /**
     * 获取DOC文件的内容列表
     */
    @Override
    public List<String> getContentDoc(File file) {
        return WordParser.searchWordDoc(file.getAbsolutePath());
    }

    /**
     * 解析Markdown文件并返回文本内容列表
     */
    @Override
    public List<String> parseMD(File file) {
        try {
            String text = MarkdownParser.convertToText(FileUtils.readFileToString(file , Charset.defaultCharset()));
            return List.of(text) ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析Excel文件并返回文本内容列表
     */
    @Override
    public List<String> parseExcel(File file) {
        String text = ExcelParser.parse(file.getAbsolutePath()) ;
        return List.of(text);
    }

    @SneakyThrows
    @Override
    public List<String> parseTxt(File targetFile) {
        return Collections.singletonList(FileUtils.readFileToString(targetFile, Charset.defaultCharset()));
    }
}
