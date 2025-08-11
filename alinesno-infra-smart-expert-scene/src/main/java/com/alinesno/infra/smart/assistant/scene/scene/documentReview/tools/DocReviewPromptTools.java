package com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools;

import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterRequestDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ReviewContentRequestDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.BusinessScenarioEnum;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.CheckRulesEnum;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.DocumentTypeEnum;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewTaskDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.GenAuditResultDto;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.ContentFormatterSceneEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.scene.enums.ContractTypeEnum;
import com.alinesno.infra.smart.scene.enums.ReviewPositionEnums;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * FormatterPromptTools
 */
@Slf4j
public class DocReviewPromptTools {

    public static final String FORMAT_REVIEW_OUTPUT_PROMPT = """
                ##列出格式
                检查出来之后，请按一定的格式列出返回json结果，按如下:
                ```json
                [
                    {
                        "id": 0 ,
                        "modificationReason": "为什么修改原因",
                        "originalContent": "原内容，需要跟原内容一模一样，不要添加其它任何说明，只需要输出要修改的原内容，便于文档定位",
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
     *
     * @param dto
     * @param scene
     * @param rule
     * @param taskInfo
     * @param taskDto
     * @return
     */
    public static String buildReviewPrompt(String contentPromptContent ,
                                           GenAuditResultDto dto,
                                           DocReviewSceneEntity scene,
                                           DocReviewRulesEntity rule,
                                           MessageTaskInfo taskInfo,
                                           DocReviewTaskDto taskDto) {

        String ruleText = rule.getRuleName() + ":" + rule.getRuleContent() ;

        String prompt = """
                ## 任务
                你是一名专业的内容检查专员，针对于指定的内容进行检查，主要检查项如下:
                %s
                
                ## 需要检查的内容
                ### 需要审查内容=%s
                """;

        String formatPrompt = String.format(prompt , ruleText, contentPromptContent) ;
        log.debug(formatPrompt);

        // 设置参数到Agent中，使得Agent可以更灵活定制Prompt
        Map<String , Object> params = new HashMap<>() ;

        ContractTypeEnum  contractType = ContractTypeEnum.getByScenarioId(taskDto.getContractType()) ;
        ReviewPositionEnums reviewPositionEnums =  ReviewPositionEnums.getByValue(taskDto.getContractType()) ;

        params.put("documentMeta" , taskDto.getContractMetadata()) ;
        params.put("ruleText" , ruleText) ;
        params.put("documentName" , taskDto.getDocumentName()) ;

        params.put("contractType" , contractType == null ? "通用合同" : contractType.getTitle() + ":" + contractType.getDesc()) ;  // 合同类型
        params.put("reviewPosition" , reviewPositionEnums == null ? "中立" :  reviewPositionEnums.getLabel()) ;  // 审核立场

        params.put("contentPromptContent" , contentPromptContent) ;
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
