package com.alinesno.infra.base.search.service.reader;

import com.agentflex.vision.qwen.QwenVisionConfig;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.ocr.OcrConfig;
import com.agentsflex.core.ocr.OcrModel;
import com.agentsflex.core.ocr.OcrRequest;
import com.agentsflex.core.ocr.OcrResponse;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.agentsflex.core.prompt.ImagePrompt;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ImageReaderServiceImpl 类是图像附件读取服务的具体实现，
 * 该类实现了 IAttachmentReaderService 接口，主要用于处理图像附件的读取操作。
 */
@Slf4j
@Getter
@Setter
@Scope("prototype")
@Service
public class ImageReaderServiceImpl extends BaseReaderServiceImpl {

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    private IndustryRoleEntity role ;

    /**
     * 此方法用于读取指定 ID 的图像附件内容。
     *
     * @param attachmentDto 要读取的图像附件的唯一标识符。
     * @param uploadData
     * @return 返回读取到的图像附件内容，若读取失败或无内容则返回 null。
     */
    @SneakyThrows
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData) {

        // 有一种是OCR识别，另外一种是大模型识别

        String modelId = uploadData.getModelId() ;
        LlmModelEntity model = llmModelService.getById(modelId) ;

        String providerCode = model.getProviderCode() ;

        if(uploadData.getRecognitionType().equals(IMAGE_OCR)){  // OCR识别
            File file = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());
            OcrConfig ocrConfig = new OcrConfig() ;

            ocrConfig.setEndpoint(model.getApiUrl());
            ocrConfig.setApiKey(model.getApiKey()) ;

            OcrModel ocrModel = llmAdapter.ocrModel(providerCode , ocrConfig) ;
            OcrRequest ocrRequest = new OcrRequest() ;
            ocrRequest.setImage(file); ;
            OcrResponse ocResponse = ocrModel.recognize(ocrRequest) ;

            return ocResponse.getResults() ;
        }else if(uploadData.getRecognitionType().equals(IMAGE_LLM)){  // LLM识别

            String previewUrl = storageConsumer.getPreviewUrl(String.valueOf(attachmentDto.getFileId())).getData();
            log.debug("LLM识别图片:{} , 图片预览地址:{}" , model , previewUrl);

            this.setRole(attachmentDto.getRole());

            QwenVisionConfig visionConfig = new QwenVisionConfig();

            visionConfig.setEndpoint(model.getApiUrl());
            visionConfig.setApiKey(model.getApiKey()) ;
            visionConfig.setModel(model.getModel()) ;

            Llm llm= llmAdapter.visionModel(providerCode , visionConfig) ;
            log.debug("visionModel:{}" , llm);

            ImagePrompt imagePrompt = new ImagePrompt("这张图片表达的是什么?" , previewUrl) ;

            CompletableFuture<String> future = getAiChatResultAsync(llm,
                    imagePrompt ,
                    attachmentDto.getTaskInfo() ,
                    attachmentDto.getOneChatId()) ;
            String output = future.get();
            log.debug("output = {}" , output);

            return output ;
        }

        return null ;

    }

    protected CompletableFuture<String> getAiChatResultAsync(Llm llm, ImagePrompt imagePrompt  , MessageTaskInfo taskInfo , String oneChatId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        // 创建一个 final 局部变量来持有 taskInfo 的引用
        final MessageTaskInfo localTaskInfo = taskInfo;
        long startTime = System.currentTimeMillis();

        llm.chatStream(imagePrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {

                AiMessage message = response.getMessage();

                System.out.println(">>>> " + message);

                FlowStepStatusDto stepDto = new FlowStepStatusDto();
                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(oneChatId);
                stepDto.setStatus(AgentConstants.STEP_PROCESS);

                if(StringUtils.hasLength(message.getContent())) {
                    stepDto.setFlowChatText(message.getContent());
                }

                if(StringUtils.hasLength(message.getReasoningContent())){
                    stepDto.setFlowReasoningText(message.getReasoningContent());
                }

                stepDto.setPrint(true);

                synchronized (localTaskInfo) {
                    localTaskInfo.setFlowStep(stepDto);
                }

                try {
                    boolean isEnd = false;
                    synchronized (localTaskInfo) {
                        if (message.getStatus() == MessageStatus.END) {
                            outputStr.set(message.getFullContent());
                            stepDto.setStatus(AgentConstants.STEP_FINISH);
                            isEnd = true;
                        }
                    }

                    streamMessagePublisher.doStuffAndPublishAnEvent(null, getRole(), localTaskInfo, localTaskInfo.getTraceBusId());

                    if (isEnd) {
                        future.complete(outputStr.get());
                    }
                } catch (Exception e) {
                    // 处理发布事件时的异常
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("消息处理失败" , throwable);
                eventStepMessage("消息处理失败", AgentConstants.STEP_FINISH , oneChatId , taskInfo) ;
                future.completeExceptionally(throwable);
            }

        }) ;

        return future;
    }

    /**
     * 流程节点消息
     * @param stepMessage
     * @param status
     */
    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo , String stepContent) {

        FlowStepStatusDto stepDto = new FlowStepStatusDto() ;
        stepDto.setMessage(stepMessage) ;
        stepDto.setStepId(stepId) ;
        stepDto.setStatus(status);
        stepDto.setPrint(false);

        if(StringUtils.hasLength(stepContent)){
            // 如果stepContent的内容超过2048个字符，则进行截取
            if(stepContent.length() > 2048){
                stepContent = stepContent.substring(0, 2048);
            }
            stepDto.setFlowChatText(stepContent);
        }

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null,
                getRole(),
                taskInfo,
                taskInfo.getTraceBusId()
        );

    }

    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo) {
        eventStepMessage(stepMessage, status, stepId, taskInfo, null);
    }

}