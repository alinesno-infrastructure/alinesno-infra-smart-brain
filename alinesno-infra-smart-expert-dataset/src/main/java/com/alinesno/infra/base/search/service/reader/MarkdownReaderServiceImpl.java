package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;

/**
 * MarkdownReaderServiceImpl 类是 Markdown 附件读取服务的具体实现，
 * 它实现了 IAttachmentReaderService 接口，主要负责读取 Markdown 格式的附件。
 * 此类提供了读取指定 Markdown 附件内容的功能。
 */
public class MarkdownReaderServiceImpl implements IAttachmentReaderService {

    /**
     * 该方法用于读取指定 ID 的 Markdown 附件内容。
     *
     * @param attachmentId 要读取的 Markdown 附件的唯一标识符。
     * @return 返回读取到的 Markdown 附件内容，如果读取失败或者附件无内容则返回 null。
     */
    @Override
    public String readAttachment(String attachmentId) {
        return null;
    }
}