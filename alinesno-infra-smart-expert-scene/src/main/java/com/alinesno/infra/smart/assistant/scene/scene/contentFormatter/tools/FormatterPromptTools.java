package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterRequestDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ReviewContentRequestDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.BusinessScenarioEnum;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.CheckRulesEnum;
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

    /**
     * 限制和示例
     */
    public static final String FORMAT_REVIEW_RULE_AND_EXAMPLE = """
                ##原文内容要求和限制，这里主要是为了更好的定位和搜索文档内容
                - 原文内容不要太长，规避无法定位内容
                - 原文内容前后不能包含有序号、括号、中英文短横线、中英文冒号、换行字符等特殊字符
                - 不要出现连续的排序内容，如果有多个，应该分开
                
                ### 以下原文档内容错误示例说明
                1. 错误:–掌握测试整体知识体系，熟悉整体学习路线
                   正确:掌握测试整体知识体系，熟悉整体学习路线
                   错误原因：带了中英文短横线
                   
                2. 错误:（预计60分钟）以学生管理系统为例子，功能学生人员的curd技术
                   正确:以学生管理系统为例子，功能学生人员的curd技术
                   错误原因：带了中英文括号
                   
                3. 错误:1.建立后端工程 2.建立前端工程
                   正确:建立后端工程
                   正确:建立前端工程
                   错误原因：连续的排序内容没有分开，应该分成两次审核
                   
                4. 错误:打包容器工具：docker（使用服务器或者虚拟机打包）
                   正确:docker
                   错误原因：带有中文冒号和中英文括号
                   
                5. 错误:2.3项目开发质保期限要求：项目验收后1年。
                   正确:项目开发质保期限要求
                   错误原因：带有中文冒号
                   
                6. 错误:质保金："渠道分销系统"在甲方支付验收款后的【60】个自然日内支付项目开发总额的剩余百分之十
                   正确:渠道分销系统"在甲方支付验收款后的【60】个自然日内支付项目开发总额的剩余百分之十
                   错误原因：带有中文冒号
            """ ;

    /**
     * 检查结果输出
     */
    public static final String FORMAT_REVIEW_OUTPUT_PROMPT = """
                ##列出格式
                检查出来之后，请按一定的格式列出返回json结果，按如下:
                ```json
                [
                    {
                        "id": 0 ,
                        "modificationReason": "为什么修改原因",
                        "originalContent": "原内容",
                        "riskLevel": "风险高中低,使用high/medium/low表示",
                        "suggestedContent": "建议修改的内容"
                    }
                ]
                ```
                """;

    public static String buildPrompt(ContentFormatterRequestDTO dto, ContentFormatterSceneEntity entity , TemplateEntity templateEntity, MessageTaskInfo taskInfo) {

        String paramFormat = templateEntity.getParamFormat() ;
        String contentPromptContent = dto.getContentPromptContent() ; // entity.getContentPromptContent() ;
        String contents = DocumentTypeEnum.getLabelByValues(dto.getDocumentType()) ;
        String scenes = BusinessScenarioEnum.getLabelByValues(dto.getBusinessScenario()) ;
        String requirement = dto.getRequirement() ;

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
                ### 生成要求=%s
                
                ## 限制
                - 严格遵守业务场景和文书的内容，使得符合文书类型和业务场景
                - 如果缺少某个字段内容，可以结合上下文给出合理内容建议
                - 不要删除或者减少原来的内容，内容不能减少
                - 不要改变原来的内容，也不需要加入其它的内容
                """;

        String formatPrompt = String.format(prompt , paramFormat , contentPromptContent , contents , scenes , requirement) ;
        log.debug(formatPrompt);

        // 设置参数到Agent中，使得Agent可以更灵活定制Prompt
        Map<String , Object> params = new HashMap<>() ;

        params.put("paramFormat" , paramFormat) ;
        params.put("contents" , contents) ;
        params.put("contentPromptContent" , contentPromptContent) ;
        params.put("scenes" , scenes) ;
        params.put("requirement" , requirement) ;

        taskInfo.setParams(params);

        return formatPrompt ;
    }

    /**
     * 构建内容检查的Prompt
     * @param dto
     * @param bySceneId
     * @param templateEntity
     * @param taskInfo
     * @return
     */
    public static String buildReviewPrompt(ReviewContentRequestDTO dto, ContentFormatterSceneEntity bySceneId, TemplateEntity templateEntity, MessageTaskInfo taskInfo) {

        String contentPromptContent = dto.getContentPromptContent() ;
        String contents = DocumentTypeEnum.getLabelByValues(dto.getDocumentType()) ;
        String scenes = BusinessScenarioEnum.getLabelByValues(dto.getBusinessScenario()) ;
        String requirement = dto.getRequirement() ;
        String ruleText = CheckRulesEnum.getAllRules(dto.getRuleIds()) ;

        String prompt = """
                ## 任务
                你是一名专业的内容检查专员，针对于指定的内容进行检查，主要检查项如下:
                %s
                
                ## 需要检查的内容
                ### 需要审查内容=%s
                ### 内容文书类型=%s
                ### 业务场景=%s
                ### 检查要求=%s
                """;


        String formatPrompt = String.format(prompt , ruleText, contentPromptContent , contents , scenes , requirement) ;
        log.debug(formatPrompt);

        // 设置参数到Agent中，使得Agent可以更灵活定制Prompt
        Map<String , Object> params = new HashMap<>() ;

        params.put("ruleText" , ruleText) ;
        params.put("contents" , contents) ;
        params.put("contentPromptContent" , contentPromptContent) ;
        params.put("scenes" , scenes) ;
        params.put("requirement" , requirement) ;

        taskInfo.setParams(params);

        return formatPrompt ;
    }

    public static String buildReviewPromptByRule(CheckRulesEnum checkRulesEnum,
                                                 ReviewContentRequestDTO dto,
                                                 ContentFormatterSceneEntity bySceneId,
                                                 TemplateEntity byId,
                                                 MessageTaskInfo taskInfo) {

        String contentPromptContent = dto.getContentPromptContent() ;
        String contents = DocumentTypeEnum.getLabelByValues(dto.getDocumentType()) ;
        String scenes = BusinessScenarioEnum.getLabelByValues(dto.getBusinessScenario()) ;
        String requirement = dto.getRequirement() ;
        String ruleText = checkRulesEnum.getRules() ;

        String prompt = """
                ## 任务
                你是一名专业的内容检查专员，针对于指定的内容进行检查，主要检查项如下:
                %s
                
                ## 需要检查的内容
                ### 需要审查内容=%s
                ### 内容文书类型=%s
                ### 业务场景=%s
                ### 检查要求=%s
                """;


        String formatPrompt = String.format(prompt , ruleText, contentPromptContent , contents , scenes , requirement) ;
        log.debug(formatPrompt);

        // 设置参数到Agent中，使得Agent可以更灵活定制Prompt
        Map<String , Object> params = new HashMap<>() ;

        params.put("ruleText" , ruleText) ;
        params.put("contents" , contents) ;
        params.put("contentPromptContent" , contentPromptContent) ;
        params.put("scenes" , scenes) ;
        params.put("requirement" , requirement) ;

        taskInfo.setParams(params);

        return formatPrompt ;
    }
}
