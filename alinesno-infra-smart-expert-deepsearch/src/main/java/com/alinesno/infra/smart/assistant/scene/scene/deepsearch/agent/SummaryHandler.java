package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

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
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.config.OutputFileFormatData;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepSearchContext;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SummaryHandler（无状态化）：显式通过 DeepSearchContext 获取运行时依赖
 */
@Component
@Slf4j
public class SummaryHandler {

    /**
     * 处理任务输出结果（异步版本，返回 CompletableFuture）
     *
     * @param llm              主对话 llm（可选，若需要指定模型可从 context 中获取）
     * @param answerOutput     所有任务拼接的输出
     * @param deepSearchOutput DeepSearchFlow.Output 实例
     * @param historyPrompt    历史 prompt（summary 会在其上追加 prompt）
     * @param goal             目标文本（仅用于描述）
     * @param context          显式传入的执行上下文（不可变）
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> handleOutputAsync(Llm llm,
                                                     StringBuilder answerOutput,
                                                     DeepSearchFlow.Output deepSearchOutput,
                                                     HistoriesPrompt historyPrompt,
                                                     String goal,
                                                     DeepSearchContext context) {

        // 将 output 放入 flow 并触发事件（使用 context）
        context.getDeepSearchFlow().setOutput(deepSearchOutput);
        context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());

        // 异步获取总结 -> 异步格式化输出
        return getAiSummaryAsync(llm, answerOutput, historyPrompt, goal, context)
                .thenCompose(summaryOutput -> {
                    log.debug("summaryOutput = {}", summaryOutput);
                    // 再触发一次事件，保证中间状态能被观察到
                    context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());
                    return outputFormatContentAsync(summaryOutput, goal, context);
                })
                .exceptionally(ex -> {
                    log.error("输出处理失败", ex);
                    throw new CompletionException(ex);
                });
    }

    /**
     * 内容格式化输出（异步版本）
     */
    private CompletableFuture<Void> outputFormatContentAsync(String summaryOutput, String goal, DeepSearchContext context) {
        return CompletableFuture.runAsync(() -> {
            try {
                var role = context.getRole();
                boolean outputFormatStatus = role.isOutputFileFormatStatus();
                if (!outputFormatStatus) {
                    return;
                }
                String outputFormat = role.getOutputFileFormatData();
                OutputFileFormatData outputFileFormatData = JSON.parseObject(outputFormat, OutputFileFormatData.class);
                if (outputFileFormatData == null) {
                    return;
                }

                List<DeepSearchFlow.FileAttachmentDto> attachments = new ArrayList<>();
                List<String> fileFormats = outputFileFormatData.getFileFormats();
                if (CollectionUtils.isEmpty(fileFormats)) {
                    return;
                }

                for (String fileFormat : fileFormats) {
                    switch (fileFormat) {
                        case "md" -> attachments.add(generateMarkdownAttachment(summaryOutput, context, goal));
                        case "html" -> {
                            try {
                                // 使用指定模型生成 HTML（调用 generatorHtmlAsync 并等待结果）
                                String modelId = outputFileFormatData.getLlmModel();
                                String htmlContent = generatorHtmlAsync(modelId, summaryOutput, goal, context).get(10, TimeUnit.MINUTES);
                                attachments.add(generateHtmlAttachment(htmlContent, context, goal));
                            } catch (Exception e) {
                                log.error("HTML 文件生成失败", e);
                                throw new RuntimeException(e);
                            }
                        }
                        // 若需支持 pdf/excel/docx 等，可在此添加相应逻辑
                        default -> log.warn("未支持的输出格式: {}", fileFormat);
                    }
                }

                if (CollectionUtils.isNotEmpty(attachments)) {
                    DeepSearchFlow.Output output = context.getDeepSearchFlow().getOutput();
                    output.setAttachments(attachments);
                    context.getDeepSearchFlow().setOutput(output);
                    context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());
                }

            } catch (Exception e) {
                log.error("内容格式化输出异常", e);
                throw new CompletionException(e);
            }
        });
    }

    // 生成 Markdown 附件并上传，返回 Attachment DTO
    private DeepSearchFlow.FileAttachmentDto generateMarkdownAttachment(String content, DeepSearchContext context, String goal) {
        try {
            File markdownFile = File.createTempFile("temp-markdown", ".md");
            FileUtils.writeStringToFile(markdownFile, content, "UTF-8");
            String storageId = context.getCloudStorageConsumer().upload(markdownFile).getData();
            return getFileAttachmentDto("MarkDown内容", "fa-brands fa-markdown", storageId, "md", goal);
        } catch (IOException e) {
            log.error("生成 Markdown 附件失败", e);
            throw new RuntimeException(e);
        }
    }

    // 生成 HTML 附件：input htmlContent 预期是包含代码块的文本（解析 code block）
    private DeepSearchFlow.FileAttachmentDto generateHtmlAttachment(String htmlContent, DeepSearchContext context, String goal) {
        List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(htmlContent);
        if (codeContentList.isEmpty()) {
            throw new RuntimeException("未解析到 HTML 代码块");
        }
        try {
            File htmlFile = File.createTempFile("temp-html", ".html");
            FileUtils.writeStringToFile(htmlFile, codeContentList.get(0).getContent(), "UTF-8");
            String storageId = context.getCloudStorageConsumer().upload(htmlFile).getData();
            return getFileAttachmentDto("网页内容", "fa-brands fa-html5", storageId, "html", goal);
        } catch (IOException e) {
            log.error("生成 HTML 附件失败", e);
            throw new RuntimeException(e);
        }
    }

    private DeepSearchFlow.FileAttachmentDto getFileAttachmentDto(String name, String icon, String storageId, String type, String goal) {
        DeepSearchFlow.FileAttachmentDto dto = new DeepSearchFlow.FileAttachmentDto();
        dto.setName(name);
        dto.setDesc(goal);
        dto.setId(IdUtil.getSnowflakeNextIdStr());
        dto.setIcon(icon);
        dto.setStorageId(storageId);
        dto.setType(type);
        return dto;
    }

    /**
     * 生成 HTML 内容（异步，流式调用 LLM）
     *
     * @param llmModelId 指定用于生成 HTML 的模型 id
     */
    private CompletableFuture<String> generatorHtmlAsync(String llmModelId,
                                                         String summaryOutput,
                                                         String goal,
                                                         DeepSearchContext context) {
        try {
            Llm llm = getLlmFromContext(llmModelId, context);
            CompletableFuture<String> future = new CompletableFuture<>();

            DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
            stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
            stepActionDto.setActionType(StepActionEnums.GENERATE_HTML.getActionType());
            stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

            // 记录输出步骤
            context.getRecordManager().addTaskOutputStepSingle(
                    context.getDeepSearchTask().getId(),
                    context.getDeepSearchTask().getSceneId(),
                    goal,
                    context.getDeepSearchFlow().getFlowId(),
                    stepActionDto);

            Map<String, Object> params = new java.util.HashMap<>();
            params.put("complex_task", goal);
            params.put("summary_content", summaryOutput);
            String htmlPrompt = com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.FreemarkerUtil
                    .processTemplate(com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt.SummaryPrompt.HTML_FORMAT_PROMPT, params);

            AtomicReference<String> outputRef = new AtomicReference<>("");

            llm.chatStream(htmlPrompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext ctx, AiMessageResponse response) {
                    AiMessage message = response.getMessage();
                    stepActionDto.setResult(StringUtils.hasLength(message.getContent()) ? message.getContent() : "");
                    stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent()) ? message.getReasoningContent() : "");

                    try {
                        if (message.getStatus() == MessageStatus.END) {
                            stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                            stepActionDto.setThink(message.getReasoningContent());
                            stepActionDto.setResult(message.getFullContent());
                            context.getRecordManager().markTaskOutputStepSingle(stepActionDto, StepActionStatusEnums.DONE.getKey());
                            future.complete(message.getFullContent());
                        }
                        // 更新 flow output step action
                        updateOutputStepAction(stepActionDto, context);
                    } catch (Exception e) {
                        log.error("HTML 生成流式处理异常", e);
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public void onFailure(ChatContext ctx, Throwable throwable) {
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
     * 获取 AI 总结结果（异步）
     */
    private CompletableFuture<String> getAiSummaryAsync(Llm llm,
                                                        StringBuilder answerOutput,
                                                        HistoriesPrompt historyPrompt,
                                                        String goal,
                                                        DeepSearchContext context) {
        try {
            Map<String, Object> params = new java.util.HashMap<>();
            params.put("complex_task", goal);
            params.put("planning_detail", context.getDatasetKnowledgeDocument());
            params.put("answer_output", answerOutput);
            String summaryPrompt = com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.FreemarkerUtil
                    .processTemplate(com.alinesno.infra.smart.assistant.scene.scene.deepsearch.prompt.SummaryPrompt.DEFAULT_SUMMARY_PROMPT, params);

            // 将 summary prompt 添加到 historyPrompt（流式调用）
            historyPrompt.addMessage(new HumanMessage(summaryPrompt));

            CompletableFuture<String> future = new CompletableFuture<>();

            DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
            stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());
            stepActionDto.setActionType(StepActionEnums.SUMMARY.getActionType());
            stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

            // 添加任务输出步骤记录
            context.getRecordManager().addTaskOutputStepSingle(
                    context.getDeepSearchTask().getId(),
                    context.getDeepSearchTask().getSceneId(),
                    goal,
                    context.getDeepSearchFlow().getFlowId(),
                    stepActionDto);

            llm.chatStream(historyPrompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext ctx, AiMessageResponse response) {
                    AiMessage message = response.getMessage();
                    stepActionDto.setResult(StringUtils.hasLength(message.getContent()) ? message.getContent() : "");
                    stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent()) ? message.getReasoningContent() : "");

                    try {
                        if (message.getStatus() == MessageStatus.END) {
                            stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                            stepActionDto.setThink(message.getReasoningContent());
                            stepActionDto.setResult(message.getFullContent());
                            context.getRecordManager().markTaskOutputStepSingle(stepActionDto, StepActionStatusEnums.DONE.getKey());
                            future.complete(message.getFullContent());
                        }
                        updateOutputStepAction(stepActionDto, context);
                    } catch (Exception e) {
                        log.error("总结流式处理异常", e);
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public void onFailure(ChatContext ctx, Throwable throwable) {
                    log.error("总结生成失败", throwable);
                    future.completeExceptionally(throwable);
                }
            });

            return future.orTimeout(10, TimeUnit.MINUTES);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // 更新 Output 步骤事件（通过 context）
    private void updateOutputStepAction(DeepSearchFlow.StepAction stepActionDto, DeepSearchContext context) {
        DeepSearchFlow.Output output = context.getDeepSearchFlow().getOutput();
        if (output == null) {
            output = new DeepSearchFlow.Output();
        }
        output.addStepAction(stepActionDto);
        context.getDeepSearchFlow().setOutput(output);
        context.getStepEventUtil().eventStepMessage(context.getDeepSearchFlow(), context.getRole(), context.getTaskInfo());
    }

    // 从 context 提供的 llmModelService / llmAdapter 中构造 Llm
    private Llm getLlmFromContext(String modelId, DeepSearchContext context) {
        var llmModel = context.getLlmModelService().getById(modelId);
        if (llmModel == null) {
            throw new IllegalArgumentException("模型未配置或者不存在: " + modelId);
        }
        LlmConfig config = new LlmConfig();
        config.setEndpoint(llmModel.getApiUrl());
        config.setApiKey(llmModel.getApiKey());
        config.setModel(llmModel.getModel());
        return context.getLlmAdapter().getLlm(llmModel.getProviderCode(), config);
    }
}