package com.alinesno.infra.smart.assistant.service;

/**
 * 附件内容解析和读取
 */
public interface IAttachmentReaderService {

    /**
     * 读取附件内容
     * @param attachmentId
     * @return
     */
    String readAttachment(String attachmentId);

}
