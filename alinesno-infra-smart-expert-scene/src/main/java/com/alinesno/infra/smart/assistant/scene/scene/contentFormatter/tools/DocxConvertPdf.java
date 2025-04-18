//package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;
//
//import com.documents4j.api.DocumentType;
//import com.documents4j.api.IConverter;
//import com.documents4j.job.LocalConverter;
//
//import java.io.*;
//
//public class DocxConvertPdf {
//
//    public static void convert(File inputWord , File outputFile){
//        try  {
//            InputStream docxInputStream = new FileInputStream(inputWord);
//            OutputStream outputStream = new FileOutputStream(outputFile);
//            IConverter converter = LocalConverter.builder().build();
//            converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
