package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ReviewRequestDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.BusinessScenarioEnum;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.DocumentTypeEnum;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.ContentFormatterSceneEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * FormatterPromptTools
 */
@Slf4j
public class FormatterPromptTools {

    public static String buildPrompt(ReviewRequestDTO dto, ContentFormatterSceneEntity entity , TemplateEntity templateEntity, MessageTaskInfo taskInfo) {

        String paramFormat = templateEntity.getParamFormat() ;
        String contentPromptContent = dto.getContentPromptContent() ; // entity.getContentPromptContent() ;
        String contents = DocumentTypeEnum.getLabelByValues(dto.getDocumentType()) ;
        String scenes = BusinessScenarioEnum.getLabelByValues(dto.getBusinessScenario()) ;

        String prompt = """
                ## 任务
                你是一名专业的文档梳理专员，你的职责是调整文档符合指定的格式，你会获取到一份需要整理的内容，然后根据这份内容整理成指定的json格式，现在需要你调整成以下json格式，
                方便后期程序解析，最终请你下面的格式进行直接输出，不要输出其他信息，格式要求如下:
                ```json
                %s
                ```
                ## 需要整理的内容
                ### 需要整理内容=%s
                ### 内容文书类型=%s
                ### 业务场景=%s
                
                ## 限制
                - 严格遵守业务场景和文书的内容，使得符合文书类型和业务场景
                - 如果缺少某个字段内容，可以结合上下文给出合理内容建议
                - 不要删除或者减少原来的内容，内容不能减少
                - 不要改变原来的内容，也不需要加入其它的内容
                """;

        String formatPrompt = String.format(prompt , paramFormat , contentPromptContent , contents , scenes) ;
        log.debug(formatPrompt);

        // 设置参数到Agent中，使得Agent可以更灵活定制Prompt
        Map<String , Object> params = new HashMap<>() ;

        params.put("paramFormat" , paramFormat) ;
        params.put("contents" , contents) ;
        params.put("contentPromptContent" , contentPromptContent) ;
        params.put("scenes" , scenes) ;

        taskInfo.setParams(params);

        return formatPrompt ;
    }

}
