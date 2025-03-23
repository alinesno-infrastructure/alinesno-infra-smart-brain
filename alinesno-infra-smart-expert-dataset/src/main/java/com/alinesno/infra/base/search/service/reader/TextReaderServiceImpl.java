package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;

/**
 * TextReaderServiceImpl 类是文本附件读取服务的具体实现，
 * 该类实现了 IAttachmentReaderService 接口，主要负责处理文本格式附件的读取操作。
 */
public class TextReaderServiceImpl implements IAttachmentReaderService {

    /**
     * 此方法用于读取指定 ID 的文本附件内容。
     *
     * @param attachmentId 要读取的文本附件的唯一标识符。
     * @return 返回读取到的文本附件内容，若读取失败或无内容则返回 null。
     */
    @Override
    public String readAttachment(String attachmentId) {
        return null;
    }
}