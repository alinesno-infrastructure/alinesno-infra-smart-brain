package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * MarkdownReaderServiceImpl 类是 Markdown 附件读取服务的具体实现，
 * 它实现了 IAttachmentReaderService 接口，主要负责读取 Markdown 格式的附件。
 * 此类提供了读取指定 Markdown 附件内容的功能。
 */
@Scope("prototype")
@Service
public class MarkdownReaderServiceImpl extends BaseReaderServiceImpl {

    /**
     * 该方法用于读取指定 ID 的 Markdown 附件内容。
     *
     * @param attachmentDto 要读取的 Markdown 附件的唯一标识符。
     * @param uploadData
     * @return 返回读取到的 Markdown 附件内容，如果读取失败或者附件无内容则返回 null。
     */
    @SneakyThrows
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData) {
        File file = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());
        // 读取文件内容
        if (file != null) {
            String content = FileUtils.readFileToString(file, "UTF-8") ;
            return cleanText(content);
        }
        return null ;
    }
}