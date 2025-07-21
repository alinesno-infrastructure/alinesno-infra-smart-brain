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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * 文档分析工具
 */
@Slf4j
@Component
public class AnalysisTool {

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
     * @param file 要分析的Word文档文件
     * @return 处理后的文档内容（最多2000个字符）
     */
    public String analysisDocumentBaseContent(File file) {
        log.debug("documentId = {}", file.getAbsoluteFile());

        String content = "";
        String fileName = file.getName().toLowerCase();

        try {
            if (fileName.endsWith(".docx")) {
                // 处理.docx文件
                try (XWPFDocument document = new XWPFDocument(new FileInputStream(file));
                     XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    content = extractor.getText();
                }
            } else if (fileName.endsWith(".doc")) {
                // 处理.doc文件
                try (HWPFDocument document = new HWPFDocument(new FileInputStream(file));
                     WordExtractor extractor = new WordExtractor(document)) {
                    content = extractor.getText();
                }
            } else {
                log.warn("不支持的文件格式: {}", fileName);
                return "";
            }

            // 清理文本内容
            content = BaseReaderServiceImpl.cleanText(content);

            // 截取前2000个字符
            if (content.length() > maxLength) {
                content = content.substring(0, maxLength);
            }

        } catch (IOException e) {
            log.error("解析Word文档失败: {}", e.getMessage());
        }

        return content;
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
                         "riskLevel": "风险高中低,使用high/medium/low表示",
                         "ruleContent": "检查的主要内容，详细说明内容信息"
                     }
                  ]
                  ```
                  示例数据：
                  ```json
                  [
                    {
                        "ruleName": "在合同主体条款中，合同主体信息是否准确",
                        "riskLevel": "medium",
                        "ruleContent": "审查合同主体信息完整性、准确性：自然人核对姓名、身份证号等；法人组织查名称、统一信用代码等；个体户确认字号及经营者信息。确保主体资格合法：赠与人行为能力或监护人追认；法人须合法存续，分支结构需授权。"
                    },
                    {
                        "ruleName": "在合同引用法律条款中，确保引用法律文件名称的准确性和有效性",
                        "riskLevel": "medium",
                        "ruleContent": "合同审查要点：核实法律文件有效性，更新失效引用（如旧法被《民法典》取代）；使用法律全称，修正简称误差；明确风险提示及修订建议；依托官方资源验证，确保引用准确最新，保障合同合法性。",
                    },
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
