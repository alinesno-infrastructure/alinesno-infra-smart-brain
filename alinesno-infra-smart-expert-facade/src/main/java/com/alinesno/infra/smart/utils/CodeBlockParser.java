package com.alinesno.infra.smart.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于解析代码块的工具类。
 */
@Slf4j
public class CodeBlockParser {

    /**
     * 解析JSON代码块场景
     * @param output
     * @return
     */
    public static List<CodeContent> parseJSONObjectCodeBlocks(String output) {
        List<CodeContent> codeBlocks = new ArrayList<>();

        // 先直接解析JSON结构
        try{
            if(StringUtils.isNotBlank(output) && output.trim().startsWith("{")){
                JSONObject jsonObject = JSONObject.parseObject(output.trim()) ; // 校验JSON结构是否正常
                codeBlocks.add(new CodeContent("json", jsonObject.toJSONString()));
                return codeBlocks ;
            }else{
                return parseCodeBlocks(output);
            }
        }catch (Exception e) {
            log.error("JSON解析异常", e);
            return parseCodeBlocks(output);
        }

    }

    /**
     * 从给定内容中解析代码块。 // TODO 处理计算机语言识别不是特别准确的问题
     *
     * @param content 要提取代码块的内容。
     * @return 包含解析后代码块的 TaskContentDto.CodeContent 对象列表。
     */
    public static List<CodeContent> parseCodeBlocks(String content) {

        List<CodeContent> codeBlocks = new ArrayList<>();

        // 先判断内容是否是以```json开头的，如果是的话则直接截取前后字符即可
        if(StringUtils.isNotBlank(content) && content.trim().startsWith("```json")){
            int endIndex = content.trim().length() ;
            String block = content.trim().substring(3, endIndex-3);
            int langIndex = block.indexOf("\n");
            if (langIndex != -1) {
                String lang = block.substring(0, langIndex).trim();
                String code = block.substring(langIndex).trim();
                codeBlocks.add(new CodeContent(lang, code));
            }

            return codeBlocks;
        }

        if(StringUtils.isNotBlank(content)) {

            String regex = "```(.*?)```"; // 用于匹配代码块的正则表达式
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String block = matcher.group(1).trim();
                int langIndex = block.indexOf("\n");
                if (langIndex != -1) {
                    String lang = block.substring(0, langIndex).trim();
                    String code = block.substring(langIndex).trim();
                    codeBlocks.add(new CodeContent(lang, code));
                }
            }

        }

        if(codeBlocks.isEmpty()){
            FileType fileType = LanguageIdentifier.identifyLanguage(content) ;

            if(!fileType.isUnknown()){  // 针对于内容结果为yaml或者sql直接返回的场景
                codeBlocks.add(new CodeContent(fileType.getValue(), content));
            }
        }

        return codeBlocks;
    }


}
