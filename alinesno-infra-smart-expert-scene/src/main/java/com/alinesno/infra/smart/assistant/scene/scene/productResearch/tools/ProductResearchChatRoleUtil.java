package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.image.*;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.message.AiMessage;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.role.llm.ModelAdapterLLM;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ChatEditorDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ImageGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.TextChatEditorDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.scene.entity.LongTextTaskEntity;
import com.alinesno.infra.smart.scene.entity.ProjectTaskEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import jodd.util.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 文章生成工具类
 */
@Slf4j
@Component
public class ProductResearchChatRoleUtil {

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ILLmAdapterService llmAdapterService ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private ModelAdapterLLM modelAdapterLLM ;

    @Autowired
    private IProjectTaskService projectTaskService;

    @Autowired
    private IAgentSceneService agentSceneService ;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    private static final String PROMPT_TEXT = """
            ##角色=你是一名文章修改专员，有资深的修改经验，现在根据用户需求修改文章内容或者某个片段
            ##原整篇文章=%s
            ##要修改的内容=%s
            ##修改要求=%s
            ##限制
            - 只返回修改的结果，其它不相关内容不要返回
            """;

    @SneakyThrows
    public void chat(IndustryRoleDto roleDto, TextChatEditorDto dto, PermissionQuery query) {

        AgentSceneEntity agentSceneEntity =  agentSceneService.getByRoleAndScene(roleDto.getId() , SceneEnum.PRODUCT_RESEARCH.getSceneInfo().getId()) ;
        LlmModelEntity modelEntity = llmModelService.getById(agentSceneEntity.getLlmModelId()) ;

        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(modelEntity.getApiUrl());
        llmConfig.setApiKey(modelEntity.getApiKey()) ;
        llmConfig.setApiSecret(modelEntity.getSecretKey());
        llmConfig.setModel(modelEntity.getModel());

        String modelProvider = modelEntity.getProviderCode() ;

        Llm llm = llmAdapterService.getLlm(modelProvider, llmConfig);

        IndustryRoleEntity role = roleService.getById(roleDto.getId());
        long messageId = IdUtil.getSnowflakeNextId() ;

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setRoleId(roleDto.getId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());

        // 处理附件
        ProjectTaskEntity projectTaskEntity = projectTaskService.getById(dto.getTaskId());
        if (StringUtil.isNotBlank(projectTaskEntity.getAttachments())) {
            List<Long> attachmentIds = Arrays.stream(projectTaskEntity.getAttachments().split(","))
                    .map(Long::parseLong)
                    .toList();
            List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
            taskInfo.setAttachments(attachments);
        }

        String articlePreContent = extractRelevantText(dto.getArticleContent() , dto.getModifyText()) ;
        String prompt = String.format(PROMPT_TEXT , articlePreContent , dto.getModifyText() ,  dto.getMessage()) ;

        CompletableFuture<AiMessage> future = modelAdapterLLM.getSingleAiChatResultAsync(llm, role, prompt , taskInfo , messageId) ;

        AiMessage message = future.get();
        log.debug("output = {}" , message.getFullContent());
    }

    /**
     * 提取文本片段
     * @param articleContent 原始文章内容
     * @param modifyText 要查找的文本片段
     * @return 返回包含 modifyText 前后 300 字符的文本，或前 600 字符（如果找不到）
     */
    public static String extractRelevantText(String articleContent, String modifyText) {
        // 1. 处理 articleContent 为空的情况
        if (articleContent == null || articleContent.isEmpty()) {
            return "【提示】文章内容为空，无法提取文本片段";
        }

        // 2. 处理 modifyText 为空的情况
        if (modifyText == null || modifyText.isEmpty()) {
            int end = Math.min(articleContent.length(), 600);
            return "【提示】未提供 modifyText，返回前 600 字符：\n" + articleContent.substring(0, end);
        }

        // 3. 查找 modifyText 的位置
        int modifyIndex = articleContent.indexOf(modifyText);

        if (modifyIndex != -1) {
            // 计算前后 300 字符的范围，确保包含 modifyText
            int start = Math.max(0, modifyIndex - 300);
            int end = Math.min(articleContent.length(), modifyIndex + modifyText.length() + 300);

            // 截取内容
            String beforeText = articleContent.substring(start, modifyIndex);
            String afterText = articleContent.substring(modifyIndex + modifyText.length(), end);

            // 拼接结果，并标注 modifyText 的位置
            return "【前 300 字符】" + beforeText +
                    "\n【modifyText】" + modifyText +
                    "\n【后 300 字符】" + afterText;
        } else {
            // 未找到 modifyText，截取前 600 字符
            int end = Math.min(articleContent.length(), 600);
            return "【提示】未找到 modifyText，返回前 600 字符：\n" + articleContent.substring(0, end);
        }
    }

}
