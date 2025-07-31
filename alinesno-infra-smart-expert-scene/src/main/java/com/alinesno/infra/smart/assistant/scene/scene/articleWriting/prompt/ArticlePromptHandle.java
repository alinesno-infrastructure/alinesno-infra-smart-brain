package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.prompt;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ArticleGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ImageGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ReWriterArticleGeneratorDTO;
import com.alinesno.infra.smart.scene.entity.ArticleTemplateEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ArticlePromptHandle {

    public static String generatorPrompt(ArticleGeneratorDTO dto, ArticleTemplateEntity templateEntity) {

        JSONObject articleConfig = dto.getArticleConfig();

        StringBuilder otherText = new StringBuilder(); // 将articleConfig转换成map，如果存在，则拼接成Key:value值说明
        if(articleConfig != null){
            for (String key : articleConfig.keySet()) {
                Object value = articleConfig.get(key);
                if (value != null && StringUtils.isNoneBlank(value+"")) {
                    otherText.append(key).append(":").append(value).append("\r\n");
                }
            }
        }

        StringBuilder prompt = new StringBuilder() ;
        String systemPrompt = templateEntity.getSystemPrompt() ;

        if(StringUtils.isNoneBlank(systemPrompt)){
            prompt.append(systemPrompt).append("\r\n");
        }

        if(StringUtils.isNoneBlank(dto.getPromptText())){
            prompt.append(dto.getPromptText()).append("\r\n");
        }

        if (StringUtils.isNoneBlank(otherText)) {
            prompt.append("其它要求:")
                    .append(otherText);
        }

        return prompt.toString() ;
    }

    /**
     * 重写文章
     *
     * @param dto
     * @param content
     * @return
     */
    public static String generatorReWriterPrompt(ReWriterArticleGeneratorDTO dto, String content) {

        String prompt = """
                ##角色:你是一名文章修改和润色专家，请将下面的文章内容按要求重新润色，并返回文章内容。
                ##原文章内容:%s
                ##修改或者润色要求:%s
                ##限制：
                - 只返回润色之后的文章内容
                - 返回markdown格式
                """;

        return String.format(prompt, content, dto.getPromptText());
    }

    /**
     * 生成图片
     * @param dto
     * @param content
     * @return
     */
    public static String generatorImagePrompt(ImageGeneratorDTO dto, String content) {
        String prompt = """
                ##角色:你作为一名AI图片生成模型，请将下面的内容生成图片，并返回图片。
                ##内容:%s
                ##要求:%s
                """;
        return String.format(prompt, content, dto.getPrompt());
    }
}
