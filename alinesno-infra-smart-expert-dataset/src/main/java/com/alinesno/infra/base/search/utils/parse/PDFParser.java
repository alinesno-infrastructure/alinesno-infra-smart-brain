package com.alinesno.infra.base.search.utils.parse;

import com.alinesno.infra.base.search.utils.TextParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

@Slf4j
public class PDFParser extends TextParser {

    /**
     * @param path pdf文件的路径
     * @return  pdf中的文本信息，但是由于pdf本身格式的问题，导致文本的顺序可能会错乱。
     */
    public static String parsePDF(String path){

        StringBuilder result = new StringBuilder();
        try{
            PDDocument document = PDDocument.load(new File(path));
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            for (int p = 1; p <= document.getNumberOfPages(); ++p)
            {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String text = stripper.getText(document);
                result.append(text);
            }
        }catch(Exception e){
            log.error("服务运行异常:{}" , e.getMessage());
        }

        return result.toString();
    }

}
