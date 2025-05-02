package com.alinesno.infra.base.search.utils.parse;

import com.alinesno.infra.base.search.service.reader.BaseReaderServiceImpl;
import com.alinesno.infra.base.search.utils.TextParser;
import com.spire.doc.Document;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于解析Word文件内容
 */
@Slf4j
public class WordParser extends TextParser {

    /**
     * 读取word文档中，docx后缀的文件
     * @param filePath
     * @return
     */
    public static List<String> searchWord(String filePath) {
        List<String> docList = new ArrayList<>();
        File tempFile = new File(filePath);
        try {
            Document doc = new Document();
            doc.loadFromFile(tempFile.toString());
            String content = doc.getText();

            docList.add(BaseReaderServiceImpl.cleanText(content));
        } catch (Exception e) {
            log.error("解析异常:{}", e.getMessage());
        }
        return docList;
    }

    //读取word文档中，doc后缀的文件
    public static List<String> searchWordDoc(String filePath){
//        List<String> docList = new ArrayList<String>();
//        String content=null;
//        //读取字节流，读取文件路径
//        InputStream input = null;
//        try {
//            input = new FileInputStream(new File(filePath));
//            WordExtractor wex = new WordExtractor(input);
//            content = wex.getText();
//            docList.add(content);
//        } catch (Exception e) {
//            log.error("解析异常:{}" , e.getMessage());
//        }
//        return docList;
        return searchWord(filePath);
    }

    public static List<String> searchWordDocX(String filePath){
//        //读取文件路径
//        OPCPackage opcPackage = null;
//        String content = null;
//        List<String> docxList = new ArrayList<String>();
//        try {
//            opcPackage = POIXMLDocument.openPackage(filePath);
//            XWPFDocument xwpf = new XWPFDocument(opcPackage);
//            POIXMLTextExtractor poiText = new XWPFWordExtractor(xwpf);
//            content = poiText.getText();
//            docxList.add(content);
//        } catch (IOException e) {
//            log.error("解析异常:{}" , e.getMessage());
//        }
//        return docxList;
        return searchWord(filePath);
    }
}