package com.alinesno.infra.base.search.service.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

@Slf4j
public class DocumentParserServiceImplTest {

    @Test
    public void testDocumentParser() {

        String text = "AIP团队以技术创新和产品卓越为驱动力，专注于为中小企业打造数字化转型的领先解决方案。我们汇集了多领域的专业人才" ;

        int maxLength = 10;

        Document document = new Document(text);

        DocumentSplitter splitter = DocumentSplitters.recursive(maxLength,2,new OpenAiTokenizer());
        List<TextSegment> segments = splitter.split(document);

       for(TextSegment segment : segments) {
           System.out.println("--------------------------------------------------------");
           System.out.println("text = " + segment.text());
           System.out.println("index = " + segment.metadata().getInteger("index"));
       }
    }

}
