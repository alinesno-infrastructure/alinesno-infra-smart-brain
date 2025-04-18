package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import com.aspose.words.Document;
import com.aspose.words.PdfSaveOptions;
import lombok.SneakyThrows;

import java.io.*;

public class WordToPdfConverter {

    /**
     * word转换为pdf
     * @param inputWord word文件输入流
     * @param outputFile pdf文件输出流
     * @throws Exception
     */
    @SneakyThrows
    public static void convertToPdf(File inputWord , File outputFile) {

        InputStream docxInputStream = new FileInputStream(inputWord);
        OutputStream outputStream = new FileOutputStream(outputFile);

        //通过aspose-words.jar中的类转换文件
        Document wordDoc = new Document(docxInputStream);
        PdfSaveOptions pso = new PdfSaveOptions();
        wordDoc.save(outputStream, pso);
    }

}
