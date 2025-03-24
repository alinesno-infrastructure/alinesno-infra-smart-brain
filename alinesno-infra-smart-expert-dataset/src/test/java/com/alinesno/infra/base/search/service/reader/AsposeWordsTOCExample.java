//package com.alinesno.infra.base.search.service.reader;
//
//import com.aspose.words.*;
//
//import java.io.File;
//import java.io.IOException;
//
//public class AsposeWordsTOCExample {
//    public static void main(String[] args) {
//        try {
//            // 加载文档
//            Document doc = new Document("your_file.docx");
//
//            // 获取目录
//            NodeCollection fields = doc.getRange().getFields();
//            for (Field field : (Iterable<Field>) fields) {
//                if (field.getType() == FieldType.FIELD_TOC) {
//                    // 打印目录的文本内容
//                    System.out.println(field.getResult());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}