package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;

/**
 * 附件内容解析和读取
 */
public interface IAttachmentReaderService {

    /**
     * 读取附件内容
     *
     * @param attachmentDto
     * @param uploadData
     * @return
     */
    String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData);

}
