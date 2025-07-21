package com.alinesno.infra.base.search.utils.parse;

import com.alinesno.infra.base.search.service.reader.BaseReaderServiceImpl;
import com.alinesno.infra.base.search.utils.TextParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于解析Word文件内容
 */
@Slf4j
public class WordParser extends TextParser {

    /**
     * 读取word文档内容（支持.doc和.docx）
     * @param filePath 文件路径
     * @return 文档内容列表
     */
    public static List<String> searchWord(String filePath) {
        List<String> docList = new ArrayList<>();
        File file = new File(filePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            String content;

            if (filePath.toLowerCase().endsWith(".docx")) {
                // 处理.docx文件
                XWPFDocument document = new XWPFDocument(fis);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                content = extractor.getText();
                extractor.close();
            } else if (filePath.toLowerCase().endsWith(".doc")) {
                // 处理.doc文件
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                content = extractor.getText();
                extractor.close();
            } else {
                log.warn("不支持的文件格式: {}", filePath);
                return docList;
            }

            docList.add(BaseReaderServiceImpl.cleanText(content));
        } catch (IOException e) {
            log.error("解析Word文件异常: {}", e.getMessage());
        }

        return docList;
    }

    // 读取.doc文件
    public static List<String> searchWordDoc(String filePath) {
        return searchWord(filePath);
    }

    // 读取.docx文件
    public static List<String> searchWordDocX(String filePath) {
        return searchWord(filePath);
    }
}