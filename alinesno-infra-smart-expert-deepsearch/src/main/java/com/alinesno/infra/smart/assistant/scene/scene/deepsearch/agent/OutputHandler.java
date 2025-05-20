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
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import freemarker.template.TemplateException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 任务输出结果处理
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
public class OutputHandler extends BaseHandler {

    private String goal ;

    @Autowired
    private ILLmAdapterService llmAdapter ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    /**
     * 处理任务输出结果
     * @param answerOutput
     * @param deepSearchOutput
     */
    @SneakyThrows
    public void handleOutput(Llm llm, StringBuilder answerOutput, DeepSearchFlow.Output deepSearchOutput , HistoriesPrompt historyPrompt , String goal) {

        setGoal(goal);

        deepSearchOutput.setName("内容总结");
        deepSearchOutput.setDescription("将根据目标生成多个目标结构.");

        getDeepSearchFlow().setOutput(deepSearchOutput);
        getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());

        CompletableFuture<String> future = getAiSummaryAsync(llm, answerOutput, historyPrompt, goal);
        String summaryOutput = future.get();

        log.debug("summaryOutput = {}" , summaryOutput);

        getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());

        // 内容格式化输出
        outputFormatContent(summaryOutput);

    }

    /**
     * 内容格式化输出
     * @param summaryOutput
     */
    @SneakyThrows
    private void outputFormatContent(String summaryOutput) {

        IndustryRoleEntity role = getRole() ;
        boolean outputFormatStatus = role.isOutputFileFormatStatus() ;
        if (outputFormatStatus) {
            String outputFormat = role.getOutputFileFormatData() ;
            OutputFileFormatData outputFileFormatData = JSON.parseObject(outputFormat, OutputFileFormatData.class) ;

            if(outputFileFormatData != null){

                List<DeepSearchFlow.FileAttachmentDto> attachments =  new ArrayList<>() ;

                List<String> fileFormats = outputFileFormatData.getFileFormats() ;
                for (String fileFormat : fileFormats) {

                    switch (fileFormat) {
                        case "md" -> {
                            // 将htmlContent输入到本地临时文件
                            File markdownFile = File.createTempFile("temp-markdown", ".md");

                            // 使用 FileUtils 写入内容，指定 UTF-8 编码
                            FileUtils.writeStringToFile(markdownFile, summaryOutput, "UTF-8");
                            String storageId = cloudStorageConsumer.upload(markdownFile).getData();

                            DeepSearchFlow.FileAttachmentDto fileAttachmentDto = getFileAttachmentDto("MarkDown内容", "fa-brands fa-markdown", storageId, "md");

                            attachments.add(fileAttachmentDto);
                        }

//                        case "excel" -> {
//                            String excelFileUrl = "";
//                        }
//                        case "pdf" -> {
//                            String pdfFileUrl = "";
//                        }
//                        case "docx" -> {
//                            String docxFileUrl = "";
//                        }

                        case "html" -> {

                            CompletableFuture<String> future = generatorHtmlAsync(outputFileFormatData.getLlmModel() , summaryOutput, getGoal());
                            String htmlContent = future.get();

                            List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(htmlContent);
                            CodeContent codeContent = codeContentList.get(0);

                            // 将htmlContent输入到本地临时文件
                            File htmlFile = File.createTempFile("temp-html", ".html");

                            // 使用 FileUtils 写入内容，指定 UTF-8 编码
                            FileUtils.writeStringToFile(htmlFile, codeContent.getContent() , "UTF-8");
                            String storageId = cloudStorageConsumer.upload(htmlFile).getData();

                            DeepSearchFlow.FileAttachmentDto fileAttachmentDto = getFileAttachmentDto("网页内容", "fa-brands fa-html5", storageId, "html");

                            attachments.add(fileAttachmentDto);
                        }
                    }

                }

                if(CollectionUtils.isNotEmpty(attachments)){
                    DeepSearchFlow.Output deepSearchOutput = getDeepSearchFlow().getOutput();
                    deepSearchOutput.setAttachments(attachments);

                    getDeepSearchFlow().setOutput(deepSearchOutput);
                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
                }

            }

        }

    }

    @NotNull
    private DeepSearchFlow.FileAttachmentDto getFileAttachmentDto(String name , String icon, String storageId, String html) {
        DeepSearchFlow.FileAttachmentDto fileAttachmentDto = new DeepSearchFlow.FileAttachmentDto();
        fileAttachmentDto.setName(name);
        fileAttachmentDto.setDesc(getGoal());
        fileAttachmentDto.setId(IdUtil.getSnowflakeNextIdStr());
        fileAttachmentDto.setIcon(icon);
        fileAttachmentDto.setStorageId(storageId);
        fileAttachmentDto.setType(html);
        return fileAttachmentDto;
    }

    /**
     * 处理输出
     * @param llmModel
     * @param summaryOutput
     * @param goal
     * @return
     */
    @SneakyThrows
    private CompletableFuture<String> generatorHtmlAsync(String llmModel, String summaryOutput, String goal) {

        Llm llm = getLlm(llmModel) ;

        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        Map<String, Object> params = new HashMap<>();

        params.put("complex_task", goal);
        params.put("summary_content", summaryOutput) ;

        String htmlPrompt = FreemarkerUtil.processTemplate(SummaryPrompt.HTML_FORMAT_PROMPT, params);

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());

        final MessageTaskInfo localTaskInfo = getTaskInfo();

        llm.chatStream(htmlPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {

                AiMessage message = response.getMessage();

                stepActionDto.setActionType(StepActionEnums.GENERATE_HTML.getActionType());
                stepActionDto.setResult(StringUtils.hasLength(message.getContent())?message.getContent():"");
                stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent())?message.getReasoningContent():"");
                stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

                try {
                    boolean isEnd = false;
                    synchronized (localTaskInfo) {
                        if (message.getStatus() == MessageStatus.END) {
                            outputStr.set(message.getFullContent());
                            isEnd = true;
                        }
                    }

                    if (isEnd) {
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                        future.complete(outputStr.get());
                    }

                    DeepSearchFlow.Output planOutput = getDeepSearchFlow().getOutput() ;
                    planOutput.addStepAction(stepActionDto);
                    getDeepSearchFlow().setOutput(planOutput);

                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
                } catch (Exception e) {
                    // 处理发布事件时的异常
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("消息处理失败" , throwable);
                future.completeExceptionally(throwable);
            }

        }) ;

        return future ;
    }

    /**
     * 获取AI总结
     * @param llm
     * @param answerOutput
     * @param historyPrompt
     * @param goal
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    private CompletableFuture<String> getAiSummaryAsync(Llm llm, StringBuilder answerOutput, HistoriesPrompt historyPrompt, String goal) throws IOException, TemplateException {
        Map<String, Object> params = new HashMap<>();

        params.put("complex_task", goal);
        params.put("planning_detail", getDatasetKnowledgeDocument());
        params.put("answer_output", answerOutput) ;

        String summaryPrompt = FreemarkerUtil.processTemplate(SummaryPrompt.DEFAULT_SUMMARY_PROMPT, params);
        historyPrompt.addMessage(new HumanMessage(summaryPrompt));

        final MessageTaskInfo localTaskInfo = getTaskInfo();

        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        DeepSearchFlow.StepAction stepActionDto = new DeepSearchFlow.StepAction();
        stepActionDto.setActionId(IdUtil.getSnowflakeNextIdStr());

        llm.chatStream(historyPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {

                AiMessage message = response.getMessage();

                stepActionDto.setActionType(StepActionEnums.SUMMARY.getActionType());
                stepActionDto.setActionName(stepActionDto.getActionName() + " | " + goal);
                stepActionDto.setResult(StringUtils.hasLength(message.getContent())?message.getContent():"");
                stepActionDto.setThink(StringUtils.hasLength(message.getReasoningContent())?message.getReasoningContent():"");
                stepActionDto.setStatus(StepActionStatusEnums.DOING.getKey());

                try {
                    boolean isEnd = false;
                    synchronized (localTaskInfo) {
                        if (message.getStatus() == MessageStatus.END) {
                            outputStr.set(message.getFullContent());
                            isEnd = true;
                        }
                    }

                    if (isEnd) {
                        stepActionDto.setStatus(StepActionStatusEnums.DONE.getKey());
                        future.complete(outputStr.get());
                    }

                    DeepSearchFlow.Output planOutput = getDeepSearchFlow().getOutput() ;
                    planOutput.addStepAction(stepActionDto);
                    getDeepSearchFlow().setOutput(planOutput);

                    getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
                } catch (Exception e) {
                    // 处理发布事件时的异常
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("消息处理失败" , throwable);
                future.completeExceptionally(throwable);
            }

        }) ;

        return future;
    }

    /**
     * 获取到指定的模型
     * @return
     */
    protected Llm getLlm(String modelId) {

        LlmModelEntity llmModel = llmModelService.getById(modelId) ;
        Assert.notNull(llmModel, "模型未配置或者不存在.");

        LlmConfig config = new LlmConfig() ;

        config.setEndpoint(llmModel.getApiUrl());
        config.setApiKey(llmModel.getApiKey()) ;
        config.setModel(llmModel.getModel()) ;

        return llmAdapter.getLlm(llmModel.getProviderCode(), config);
    }
}
