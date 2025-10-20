package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.config.OutputFileFormatData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt.SummaryPrompt;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.FreemarkerUtil;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 任务输出结果处理（异步化改造，支持异常传递）
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
public class SummaryHandler extends BaseHandler {

    private String goal;

    @Autowired
    private ILLmAdapterService llmAdapter;

    @Autowired
    private ILlmModelService llmModelService;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer;

    /**
     * 处理任务输出结果（异步版本，返回 CompletableFuture）
     */
    public CompletableFuture<Void> handleOutputAsync(Llm llm, StringBuilder answerOutput,
                                                     DeepSearchFlow.Output deepSearchOutput,
                                                     HistoriesPrompt historyPrompt, String goal) {
        setGoal(goal);

        deepSearchOutput.setName("内容总结");
        deepSearchOutput.setDescription("将根据目标生成多个目标结构.");

        getDeepSearchFlow().setOutput(deepSearchOutput);
        getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole(), getTaskInfo());

        // 异步获取总结结果 -> 异步格式化输出
        return getAiSummaryAsync(llm, answerOutput, historyPrompt, goal)
                .thenCompose(summaryOutput -> {
                    log.debug("summaryOutput = {}", summaryOutput);
                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole(), getTaskInfo());
                    return outputFormatContentAsync(summaryOutput); // 异步格式化输出
                })
                .exceptionally(ex -> {
                    log.error("输出处理失败", ex);
                    throw new CompletionException(ex); // 包装异常，便于上层捕获
                });
    }

    /**
     * 内容格式化输出（异步版本）
     */
    private CompletableFuture<Void> outputFormatContentAsync(String summaryOutput) {
        return CompletableFuture.runAsync(() -> {
            try {
                IndustryRoleEntity role = getRole();
                boolean outputFormatStatus = role.isOutputFileFormatStatus();
                if (outputFormatStatus) {
                    String outputFormat = role.getOutputFileFormatData();
                    OutputFileFormatData outputFileFormatData = JSON.parseObject(outputFormat, OutputFileFormatData.class);
                    if (outputFileFormatData != null) {
                        List<DeepSearchFlow.FileAttachmentDto> attachments = new ArrayList<>();
                        List<String> fileFormats = outputFileFormatData.getFileFormats();
                        for (String fileFormat : fileFormats) {
                            switch (fileFormat) {
                                case "md":
                                    attachments.add(generateMarkdownAttachment(summaryOutput));
                                    break;
                                case "html":
                                    try {
                                        // 同步获取 HTML 生成结果（若需完全异步，可改造 generatorHtmlAsync 为非阻塞）
                                        String htmlContent = generatorHtmlAsync(outputFileFormatData.getLlmModel(), summaryOutput, goal).get(10, TimeUnit.MINUTES);
                                        attachments.add(generateHtmlAttachment(htmlContent));
                                    } catch (Exception e) {
                                        log.error("HTML 文件生成失败", e);
                                        throw new RuntimeException(e);
                                    }
                                    // 其他格式（excel/pdf/docx）类似处理...
                            }
                        }
                        if (CollectionUtils.isNotEmpty(attachments)) {
                            DeepSearchFlow.Output deepSearchOutput = getDeepSearchFlow().getOutput();
                            deepSearchOutput.setAttachments(attachments);
                            getDeepSearchFlow().setOutput(deepSearchOutput);
                            getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole(), getTaskInfo());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("内容格式化输出异常", e);
                throw new RuntimeException(e);
            }
        });
    }

    // 生成 Markdown 附件（辅助方法）
    private DeepSearchFlow.FileAttachmentDto generateMarkdownAttachment(String content) throws IOException {
        File markdownFile = File.createTempFile("temp-markdown", ".md");
        FileUtils.writeStringToFile(markdownFile, content, "UTF-8");
        String storageId = cloudStorageConsumer.upload(markdownFile).getData();
        return getFileAttachmentDto("MarkDown内容", "fa-brands fa-markdown", storageId, "md");
    }

    // 生成 HTML 附件（辅助方法）
    private DeepSearchFlow.FileAttachmentDto generateHtmlAttachment(String htmlContent) throws IOException {
        List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(htmlContent);
        if (codeContentList.isEmpty()) {
            throw new RuntimeException("未解析到 HTML 代码块");
        }
        File htmlFile = File.createTempFile("temp-html", ".html");
        FileUtils.writeStringToFile(htmlFile, codeContentList.get(0).getContent(), "UTF-8");
        String storageId = cloudStorageConsumer.upload(htmlFile).getData();
        return getFileAttachmentDto("网页内容", "fa-brands fa-html5", storageId, "html");
    }

    @NotNull
    private DeepSearchFlow.FileAttachmentDto getFileAttachmentDto(String name, String icon, String storageId, String type) {
        DeepSearchFlow.FileAttachmentDto dto = new DeepSearchFlow.FileAttachmentDto();
        dto.setName(name);
        dto.setDesc(getGoal());
        dto.setId(IdUtil.getSnowflakeNextIdStr());
        dto.setIcon(icon);
        dto.setStorageId(storageId);
        dto.setType(type);
        return dto;
    }

    /**
     * 生成 HTML 内容（保持原有异步实现）
     */
    private CompletableFuture<String> generatorHtmlAsync(String llmModel, String summaryOutput, String goal) {
        try {
            Llm llm = getLlm(llmModel);
            CompletableFuture<String> future = new CompletableFuture<>();
            AtomicReference<String> outputStr = new AtomicReference<>("");

            Map<String, Object> params = new HashMap<>();
            params.put("complex_task", goal);
            params.put("summary_content", summaryOutput);
            String htmlPrompt = FreemarkerUtil.processTemplate(SummaryPrompt.HTML_FORMAT_PROMPT, params);

            DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
            stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());

            llm.chatStream(htmlPrompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext context, AiMessageResponse response) {
                    AiMessage message = response.getMessage();
                    stepActionDto.setActionType(StepActionEnums.GENERATE_HTML.getActionType());
                    stepActionDto.setResult(StringUtils.hasLength(message.getContent()) ? message.getContent() : "");
                    stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent()) ? message.getReasoningContent() : "");
                    stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

                    try {
                        if (message.getStatus() == MessageStatus.END) {
                            stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                            future.complete(message.getFullContent());
                        }
                        updateOutputStepAction(stepActionDto);
                    } catch (Exception e) {
                        log.error("HTML 生成流式处理异常", e);
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public void onFailure(ChatContext context, Throwable throwable) {
                    log.error("HTML 生成失败", throwable);
                    future.completeExceptionally(throwable);
                }
            });
            return future.orTimeout(10, TimeUnit.MINUTES);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 获取 AI 总结结果（保持原有异步实现）
     */
    private CompletableFuture<String> getAiSummaryAsync(Llm llm, StringBuilder answerOutput,
                                                        HistoriesPrompt historyPrompt, String goal) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("complex_task", goal);
            params.put("planning_detail", getDatasetKnowledgeDocument());
            params.put("answer_output", answerOutput);
            String summaryPrompt = FreemarkerUtil.processTemplate(SummaryPrompt.DEFAULT_SUMMARY_PROMPT, params);
            historyPrompt.addMessage(new HumanMessage(summaryPrompt));

            CompletableFuture<String> future = new CompletableFuture<>();
            AtomicReference<String> outputStr = new AtomicReference<>("");
            DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
            stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());

            llm.chatStream(historyPrompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext context, AiMessageResponse response) {
                    AiMessage message = response.getMessage();
                    stepActionDto.setActionType(StepActionEnums.SUMMARY.getActionType());
                    stepActionDto.setResult(StringUtils.hasLength(message.getContent()) ? message.getContent() : "");
                    stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent()) ? message.getReasoningContent() : "");
                    stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

                    try {
                        if (message.getStatus() == MessageStatus.END) {
                            stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                            future.complete(message.getFullContent());
                        }
                        updateOutputStepAction(stepActionDto);
                    } catch (Exception e) {
                        log.error("总结流式处理异常", e);
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public void onFailure(ChatContext context, Throwable throwable) {
                    log.error("总结生成失败", throwable);
                    future.completeExceptionally(throwable);
                }
            });
            return future.orTimeout(10, TimeUnit.MINUTES);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // 更新 Output 步骤事件
    private void updateOutputStepAction(DeepSearchFlow.StepAction stepActionDto) {
        DeepSearchFlow.Output output = getDeepSearchFlow().getOutput();
        output.addStepAction(stepActionDto);
        getDeepSearchFlow().setOutput(output);
        getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole(), getTaskInfo());
    }

    protected Llm getLlm(String modelId) {
        LlmModelEntity llmModel = llmModelService.getById(modelId);
        Assert.notNull(llmModel, "模型未配置或者不存在.");

        LlmConfig config = new LlmConfig();
        config.setEndpoint(llmModel.getApiUrl());
        config.setApiKey(llmModel.getApiKey());
        config.setModel(llmModel.getModel());
        return llmAdapter.getLlm(llmModel.getProviderCode(), config);
    }
}