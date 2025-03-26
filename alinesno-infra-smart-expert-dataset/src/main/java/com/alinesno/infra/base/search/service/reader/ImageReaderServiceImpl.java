package com.alinesno.infra.base.search.service.reader;

import com.agentflex.vision.qwen.QwenVisionConfig;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.ocr.OcrConfig;
import com.agentsflex.core.ocr.OcrModel;
import com.agentsflex.core.ocr.OcrRequest;
import com.agentsflex.core.ocr.OcrResponse;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * ImageReaderServiceImpl 类是图像附件读取服务的具体实现，
 * 该类实现了 IAttachmentReaderService 接口，主要用于处理图像附件的读取操作。
 */
@Slf4j
@Service
public class ImageReaderServiceImpl  extends BaseReaderServiceImpl {

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
        File file = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());

        String modelId = uploadData.getModelId() ;
        LlmModelEntity model = llmModelService.getById(modelId) ;

        String providerCode = model.getProviderCode() ;

        if(uploadData.getRecognitionType().equals(IMAGE_OCR)){  // OCR识别
            OcrConfig ocrConfig = new OcrConfig() ;

            ocrConfig.setEndpoint(model.getApiUrl());
            ocrConfig.setApiKey(model.getApiKey()) ;

            OcrModel ocrModel = llmAdapter.ocrModel(providerCode , ocrConfig) ;
            OcrRequest ocrRequest = new OcrRequest() ;
            ocrRequest.setImage(file); ;
            OcrResponse ocResponse = ocrModel.recognize(ocrRequest) ;

            return ocResponse.getResults() ;
        }else if(uploadData.getRecognitionType().equals(IMAGE_LLM)){  // LLM识别
            log.debug("LLM识别图片:{}" , model);

            QwenVisionConfig visionConfig = new QwenVisionConfig();

            visionConfig.setEndpoint(model.getApiUrl());
            visionConfig.setApiKey(model.getApiKey()) ;
            visionConfig.setModel(model.getModel()) ;

            Llm visionModel = llmAdapter.visionModel(providerCode , visionConfig) ;
            log.debug("visionModel:{}" , visionModel);
        }

        return null ;

    }

}