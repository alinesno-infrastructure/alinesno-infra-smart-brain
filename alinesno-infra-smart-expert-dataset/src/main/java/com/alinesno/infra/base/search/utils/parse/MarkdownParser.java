package com.alinesno.infra.base.search.utils.parse;

import com.alinesno.infra.base.search.utils.TextParser;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

public class MarkdownParser extends TextParser {

    public static String convertToText(String markdown) {

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        TextContentRenderer renderer = TextContentRenderer.builder().build();

        return renderer.render(document);
    }

}
