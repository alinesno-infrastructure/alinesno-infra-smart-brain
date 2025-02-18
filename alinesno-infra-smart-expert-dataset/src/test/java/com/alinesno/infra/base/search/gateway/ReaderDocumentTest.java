package com.alinesno.infra.base.search.gateway;//package com.alinesno.infra.base.search.gateway;
//
//import cn.hutool.core.io.resource.ResourceUtil;
//import org.springframework.ai.ResourceUtils;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.reader.tika.TikaDocumentReader;
//import org.springframework.core.io.DefaultResourceLoader;
//import org.springframework.core.io.FileSystemResourceLoader;
//import org.springframework.core.io.Resource;
//
//import java.util.List;
//
///**
// * 测试使用spring ai进行文档读取
// */
//public class ReaderDocumentTest {
//
//    public static void main(String[] args) {
//
//        Resource resource = (new FileSystemResourceLoader()).getResource("1839229294572601344.pdf") ;
//
//        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
//        List<Document> documents = tikaDocumentReader.read();
//
//        System.out.println(documents);
//    }
//
//}
