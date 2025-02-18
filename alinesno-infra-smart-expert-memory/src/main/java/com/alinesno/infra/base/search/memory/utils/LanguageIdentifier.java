package com.alinesno.infra.base.search.memory.utils;

import com.alinesno.infra.base.search.memory.enums.FileType;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import java.util.regex.Pattern;

/**
 * 内容语言判断，针对于返回的内容是直接结果的情况，而不是带有```语言标识
 */
public class LanguageIdentifier {

    public static FileType identifyLanguage(String content) {
        if (isJava(content)) {
            return FileType.JAVA;
        } else if (isPython(content)) {
            return FileType.PYTHON;
        } else if (isYAML(content)) {
            return FileType.YAML;
        } else if (isMarkdown(content)) {
            return FileType.MARKDOWN;
        } else if (isSQL(content)) {
            return FileType.SQL;
        } else {
            return FileType.UNKNOWN;
        }
    }

    private static boolean isJava(String content) {

        if(StringUtils.hasLength(content)){
            // 判断是否包含Java关键字
            String javaKeywords = "\\b(package|import|class|public|private|protected|static|void|if|else|for|while|do|break|continue|return)\\b";
            if (Pattern.compile(javaKeywords).matcher(content).find()) {
                return true;
            }

            // 判断是否包含Java注释
            String javaComment = "//.*|/\\*(.|[\\n\\r])*?\\*/";
            return Pattern.compile(javaComment).matcher(content).find();
        }

        return false;
    }

    private static boolean isPython(String content) {
        if(StringUtils.hasLength(content)) {
            // 判断是否包含Python关键字
            String pythonKeywords = "\\b(and|as|assert|break|class|continue|def|del|elif|else|except|finally|for|from|global|if|import|in|is|lambda|nonlocal|not|or|pass|raise|return|try|while|with|yield)\\b";
            if (Pattern.compile(pythonKeywords).matcher(content).find()) {
                return true;
            }

            // 判断是否包含Python注释
            String pythonComment = "#.*";
            return Pattern.compile(pythonComment).matcher(content).find();
        }

        return false;
    }

    private static boolean isYAML(String content) {
        if(StringUtils.hasLength(content)) {
            try {
                Yaml yaml = new Yaml();
                yaml.load(content);
                return true;
            } catch (ParserException e) {
                return false;
            }
        }
        return false ;
    }

    private static boolean isMarkdown(String content) {
        if(StringUtils.hasLength(content)) {
            // 判断是否包含Markdown标题
            String markdownHeader = "^#+\\s.*";
            if (Pattern.compile(markdownHeader, Pattern.MULTILINE).matcher(content).find()) {
                return true;
            }

            // 判断是否包含Markdown列表
            String markdownList = "^[\\-\\*\\+]\\s.*";
            return Pattern.compile(markdownList, Pattern.MULTILINE).matcher(content).find();
        }

        return false;
    }

    private static boolean isSQL(String content) {
        if(StringUtils.hasLength(content)) {
            // 判断是否包含SQL关键字
            String sqlKeywords = "\\b(select|insert|update|delete|create|drop|alter|table|from|where|order by|group by|having|join|on|and|or)\\b";
            if (Pattern.compile(sqlKeywords, Pattern.CASE_INSENSITIVE).matcher(content).find()) {
                return true;
            }

            // 判断是否包含SQL注释
            String sqlComment = "--.*";
            return Pattern.compile(sqlComment).matcher(content).find();
        }

        return false;
    }
}
