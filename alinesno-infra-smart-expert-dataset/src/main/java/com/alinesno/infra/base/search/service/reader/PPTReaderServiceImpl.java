package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.base.search.utils.ImageRecognitionService;
import com.alinesno.infra.base.search.utils.PPTContentParser;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * PPTX文件读取服务实现类
 * PptxReaderServiceImpl
 */
@Slf4j
@Scope("prototype")
@Service
public class PPTReaderServiceImpl extends BaseReaderServiceImpl {

    @SneakyThrows
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData) {

        File pptFile ;
        if(attachmentDto.getFile() != null){
            pptFile = attachmentDto.getFile() ;
        }else{
            pptFile = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());
        }

        // 有一种是OCR识别，另外一种是大模型识别
        String modelId = uploadData.getModelId() ;
        LlmModelEntity model = llmModelService.getById(modelId) ;
        String recognitionType = uploadData.getRecognitionType() ;

        ImageRecognitionService imageRecognitionService = new ImageRecognitionService();
        imageRecognitionService.setLlmAdapter(llmAdapter);
        imageRecognitionService.setStorageConsumer(storageConsumer);

        List<PPTContentParser.SlideContent> result = PPTContentParser.getInstance(imageRecognitionService).parsePPTContent(pptFile, recognitionType , model);

        StringBuilder sb = new StringBuilder();
        for (PPTContentParser.SlideContent slide : result) {
            sb.append("Slide ");
            sb.append(slide.toString());
        }

        log.debug("PPT文件内容：{}" , sb);

        return sb.toString() ;
    }
}
