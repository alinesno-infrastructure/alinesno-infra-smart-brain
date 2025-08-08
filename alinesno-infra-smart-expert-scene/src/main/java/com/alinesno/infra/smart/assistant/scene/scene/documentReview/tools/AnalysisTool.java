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

    private static final int maxLength = 200 ;

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

}
