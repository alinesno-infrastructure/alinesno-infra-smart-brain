package com.alinesno.infra.smart.brain.utils;

import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于解析代码块的工具类。
 */
public class CodeBlockParser {

    /**
     * 从给定内容中解析代码块。
     *
     * @param content 要提取代码块的内容。
     * @return 包含解析后代码块的 TaskContentDto.CodeContent 对象列表。
     */
    public static List<TaskContentDto.CodeContent> parseCodeBlocks(String content) {
        List<TaskContentDto.CodeContent> codeBlocks = new ArrayList<>();
        String regex = "```(.*?)```"; // 用于匹配代码块的正则表达式
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String block = matcher.group(1).trim();
            int langIndex = block.indexOf("\n");
            if (langIndex != -1) {
                String lang = block.substring(0, langIndex).trim();
                String code = block.substring(langIndex).trim();
                codeBlocks.add(new TaskContentDto.CodeContent(lang, code));
            }
        }

        return codeBlocks;
    }

}
