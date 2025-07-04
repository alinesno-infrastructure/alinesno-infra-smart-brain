package com.alinesno.infra.base.search.service.reader;

import com.agentsflex.core.document.Document;
import com.agentsflex.document.parser.PdfBoxDocumentParser;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;

/**
 * PdfReaderServiceImpl 类是 PDF 附件读取服务的具体实现，
 * 它实现了 IAttachmentReaderService 接口，专门用于处理 PDF 附件的读取操作。
 * 通过该类可以根据附件的唯一标识来读取对应的 PDF 附件内容。
 */
@Slf4j
@Scope("prototype")
@Service
public class PdfReaderServiceImpl  extends BaseReaderServiceImpl {

    /**
     * 此方法用于根据给定的附件 ID 读取对应的 PDF 附件内容。
     *
     * @param attachmentDto 要读取的 PDF 附件的唯一标识符。
     * @param uploadData
     * @return 返回读取到的 PDF 附件内容，若读取失败或无内容则返回 null。
     */
    @SneakyThrows
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData) {

        File file ;
        if(attachmentDto.getFile() != null){
            file = attachmentDto.getFile() ;
        }else{
            file = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());
        }

        FileInputStream stream = new FileInputStream(file);
        PdfBoxDocumentParser parser = new PdfBoxDocumentParser();
        Document document = parser.parse(stream);

        String pdfContent = document.getContent() ;
        log.debug("pdfContent = {}" , pdfContent);

        return pdfContent ; // cleanText(document.getContent());
    }
}