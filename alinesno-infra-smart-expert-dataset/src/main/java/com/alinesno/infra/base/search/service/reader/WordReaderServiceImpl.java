package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;

/**
 * WordReaderServiceImpl 类是 Word 附件读取服务的具体实现，
 * 该类实现了 IAttachmentReaderService 接口，主要负责处理 Word 格式附件的读取操作。
 */
public class WordReaderServiceImpl implements IAttachmentReaderService {

    /**
     * 此方法用于读取指定 ID 的 Word 附件内容。
     *
     * @param attachmentId 要读取的 Word 附件的唯一标识符。
     * @return 返回读取到的 Word 附件内容，若读取失败或者附件无内容则返回 null。
     */
    @Override
    public String readAttachment(String attachmentId) {
        return null;
    }
}