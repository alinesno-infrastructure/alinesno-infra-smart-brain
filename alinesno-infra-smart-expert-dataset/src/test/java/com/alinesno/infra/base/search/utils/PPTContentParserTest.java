package com.alinesno.infra.base.search.utils;

import org.junit.Test;

import java.io.File;
import java.util.List;

public class PPTContentParserTest {

    @Test
    public void testParsePPTContent() {

        File pptFile = new File("/Users/luodong/Desktop/第1章  Python语言及数据挖掘介绍.pptx");
        List<PPTContentParser.SlideContent> result2 = PPTContentParser.getInstance(null).parsePPTContent(pptFile, null, null);

        StringBuilder sb = new StringBuilder();
        for (PPTContentParser.SlideContent slide : result2) {
            sb.append("Slide ");
            sb.append(slide.toString());
        }

        System.out.println("Text Content: " + sb);

    }
}
