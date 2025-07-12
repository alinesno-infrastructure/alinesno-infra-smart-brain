package com.alinesno.infra.base.search.utils;

import com.agentflex.vision.qwen.QwenVisionConfig;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.ocr.OcrConfig;
import com.agentsflex.core.ocr.OcrModel;
import com.agentsflex.core.ocr.OcrRequest;
import com.agentsflex.core.ocr.OcrResponse;
import com.agentsflex.core.prompt.ImagePrompt;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.File;

import static com.alinesno.infra.base.search.service.reader.BaseReaderServiceImpl.IMAGE_LLM;
import static com.alinesno.infra.base.search.service.reader.BaseReaderServiceImpl.IMAGE_OCR;

/**
 * 图片识别服务，处理OCR和大模型识别两种方式
 */
@Slf4j
@Data
public class ImageRecognitionService {

    private ILLmAdapterService llmAdapter;

    protected CloudStorageConsumer storageConsumer;

    /**
     * 根据指定的识别类型处理图片
     * @param file 待识别的图片文件
     * @param model 模型配置
     * @param recognitionType 识别类型：ocr或llm
     * @param fileId 文件ID
     * @return 识别结果字符串
     */
    public String recognizeImage(File file,
                                LlmModelEntity model,
                                String recognitionType,
                                String fileId) {
        String providerCode = model.getProviderCode();

        if (IMAGE_OCR.equals(recognitionType)) {  // OCR识别
            return processImageByOcr(file, model, providerCode);
        } else if (IMAGE_LLM.equals(recognitionType)) {  // LLM识别
            return processImageByLlm(model, providerCode, fileId);
        }

        throw new IllegalArgumentException("不支持的识别类型: " + recognitionType);
    }

    /**
     * 使用OCR技术处理图片
     */
    private String processImageByOcr(File file, LlmModelEntity model, String providerCode) {
        try {
            OcrConfig ocrConfig = new OcrConfig();
            ocrConfig.setEndpoint(model.getApiUrl());
            ocrConfig.setApiKey(model.getApiKey());

            OcrModel ocrModel = llmAdapter.ocrModel(providerCode, ocrConfig);
            OcrRequest ocrRequest = new OcrRequest();
            ocrRequest.setImage(file);
            OcrResponse ocResponse = ocrModel.recognize(ocrRequest);

            log.debug("ocResponse:{}", ocResponse);

            return ocResponse.getResults();
        } catch (Exception e) {
            log.error("OCR识别图片失败", e);
            throw new RuntimeException("OCR识别图片失败", e);
        }
    }

    /**
     * 使用大模型技术处理图片
     */
    private String processImageByLlm(LlmModelEntity model, String providerCode, String fileId) {
        try {
            String previewUrl = storageConsumer.getPreviewUrl(fileId).getData();
            log.debug("LLM识别图片:{} , 图片预览地址:{}", model, previewUrl);

            QwenVisionConfig visionConfig = new QwenVisionConfig();
            visionConfig.setEndpoint(model.getApiUrl());
            visionConfig.setApiKey(model.getApiKey());
            visionConfig.setModel(model.getModel());

            Llm llm = llmAdapter.visionModel(providerCode, visionConfig);
            log.debug("visionModel:{}", llm);

            ImagePrompt imagePrompt = new ImagePrompt("这张图片表达的是什么?", previewUrl);
            AiMessageResponse response = llm.chat(imagePrompt);
            return response.getResponse();
        } catch (Exception e) {
            log.error("LLM识别图片失败", e);
            throw new RuntimeException("LLM识别图片失败", e);
        }
    }

}