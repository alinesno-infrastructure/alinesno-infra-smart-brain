package com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.message.AiMessage;
import com.alinesno.infra.base.search.service.reader.BaseReaderServiceImpl;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.role.llm.ModelAdapterLLM;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import com.spire.doc.Document;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * 文档分析工具
 */
@Slf4j
@Component
public class AnalysisTool {

//    @Autowired
//    private IAttachmentReaderService attachmentReaderService ;

    private static final int maxLength = 10000 ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ILLmAdapterService llmAdapterService ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private ModelAdapterLLM modelAdapterLLM ;

    @Autowired
    private IAgentSceneService agentSceneService ;

    /**
     * 分析文档基础内容
     */
    public String analysisDocumentBaseContent(File file) {

        log.debug("documentId = {}" , file.getAbsoluteFile());

        //加载包含目录的Word文档
        Document doc = new Document();
        doc.loadFromFile(file.getAbsolutePath()) ;

        // 获取到所有内容，并去掉所有空格
        String content = BaseReaderServiceImpl.cleanText(doc.getText());

        // 如果content不为空，则截取前2000个字符
        if (content.length() > maxLength) {
            content = content.substring(0, maxLength);
        }

        return content ;
    }

    @SneakyThrows
    public void handleChapterMessage(WorkflowExecutionDto genContent, MessageTaskInfo taskInfo) {

        taskInfo.setTraceBusId(IdUtil.getSnowflakeNextId());

        String formatContent = """
         ##角色：你是一名文档总结专家，请总结下面文档的内容
         ##限制:
            1. 直接返回总结内容，不要包含其它不相关内容。
            2. 总结成一段话，字数不超过300个字。
         ##文档内容=%s
         """;

        taskInfo.setText(String.format(formatContent , taskInfo.getText())) ;
        AgentSceneEntity agentSceneEntity =  agentSceneService.getByRoleAndScene(taskInfo.getRoleId(), SceneEnum.DOCUMENT_REVIEW.getSceneInfo().getId()) ;

        LlmModelEntity modelEntity = llmModelService.getById(agentSceneEntity.getLlmModelId()) ;

        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(modelEntity.getApiUrl());
        llmConfig.setApiKey(modelEntity.getApiKey()) ;
        llmConfig.setApiSecret(modelEntity.getSecretKey());
        llmConfig.setModel(modelEntity.getModel());

        String modelProvider = modelEntity.getProviderCode() ;

        Llm llm = llmAdapterService.getLlm(modelProvider, llmConfig);

        IndustryRoleEntity role = roleService.getById(taskInfo.getRoleId());
        long messageId = IdUtil.getSnowflakeNextId() ;
        CompletableFuture<AiMessage> future = modelAdapterLLM.getSingleAiChatResultAsync(llm, role, taskInfo.getText() , taskInfo , messageId) ;

        AiMessage message = future.get();
        log.debug("output = {}" , message.getFullContent());

        genContent.setGenContent(message.getFullContent());
    }

    @SneakyThrows
    public void handleFormatRuleMessage(WorkflowExecutionDto genContent, MessageTaskInfo taskInfo) {

        taskInfo.setTraceBusId(IdUtil.getSnowflakeNextId());

        String formatContent = """
                ##角色：你是一个专业的文档审核规则创作智能体，非常善于针对标书文档审核规则领域，创作一个文档审核规则，并且对文档审核规则中每一部分的编写要求给出具体指导，目的是方便将创作任务拆分为多个部分，可以分配给不同的智能体去完成校验。
                     
                ##任务：请你针对用户的需求还有对应知识内容，严格遵循下面的规则要求，调取你的专业领域数据，创作一份满足用户文档审核规则，注意不是内容本身，而是文档审核规则，具体内容将会有多个智能体按照你创作的文档审核规则进行编写。
                 
                ##规则：
                 1-在创作文档审核规则的时候，请你主要【附件资料】为主，调取你这个领域的专业数据进行创作文档审核规则。
                 2-在创作文档审核规则的时候，请你平均拆分为N个部分，未来将有N个不同的智能体按照你的文档审核规则进行创作具体的文字内容。请你保证N个部分边界清晰，没有重叠，避免冲突。
                 3-在你创作的文档审核规则的N个部分的时候，每个部分需要给出一个标题，然后给出一个具体的编写这部分内容编写要求，目的是给下一步拆分后的创作智能体参考。
                 4-生成的审核规则清单不少于20条审核清单
                 5-最终请你下面的格式进行直接输出，不要输出其他信息，格式如下:
                  ```json
                  [
                     {
                         "ruleName": "无特殊格式要求，一般为详细描述审核规则具体内容的文本，是执行审核时的依据",
                         "ruleLevel": "无严格的固定格式，但通常使用能够表达规则重要程度或适用范围的词汇，如“重要”、“一般”、“次要”等，也可根据业务需求自定义有意义的级别名称",
                     }
                  ]
                  ```
                  示例数据：
                  ```json
                  [
                      {
                          "ruleName": "在合同风险负担及通知义务条款中，技术开发合同风险负担及通知义务",
                          "ruleLevel": "重要"
                      },
                      {
                          "ruleName": "在合同违约责任条款中，审查违约责任是否明确、违约金标准是否适当",
                          "ruleLevel": "一般"
                      },
                      {
                          "ruleName": "在合同法律引用条款中，确保引用法律文件名称的准确性和有效性",
                          "ruleLevel": "重要"
                      }
                  ]
                  ```
                 
                ##需求=%s
                ##附件资料=%s
                """;

        taskInfo.setText(String.format(formatContent , taskInfo.getText() , taskInfo.getFullContent())) ;
        AgentSceneEntity agentSceneEntity =  agentSceneService.getByRoleAndScene(taskInfo.getRoleId(), SceneEnum.DOCUMENT_REVIEW.getSceneInfo().getId()) ;

        LlmModelEntity modelEntity = llmModelService.getById(agentSceneEntity.getLlmModelId()) ;

        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(modelEntity.getApiUrl());
        llmConfig.setApiKey(modelEntity.getApiKey()) ;
        llmConfig.setApiSecret(modelEntity.getSecretKey());
        llmConfig.setModel(modelEntity.getModel());

        String modelProvider = modelEntity.getProviderCode() ;

        Llm llm = llmAdapterService.getLlm(modelProvider, llmConfig);

        IndustryRoleEntity role = roleService.getById(taskInfo.getRoleId());
        long messageId = IdUtil.getSnowflakeNextId() ;
        CompletableFuture<AiMessage> future = modelAdapterLLM.getSingleAiChatResultAsync(llm, role, taskInfo.getText() , taskInfo , messageId) ;

        AiMessage message = future.get();
        log.debug("output = {}" , message.getFullContent());

        genContent.setGenContent(message.getFullContent());
        genContent.setCodeContent(CodeBlockParser.parseCodeBlocks(message.getFullContent()));
    }
}
