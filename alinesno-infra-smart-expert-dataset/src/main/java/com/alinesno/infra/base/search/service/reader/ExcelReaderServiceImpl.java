package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;

/**
 * ExcelReaderServiceImpl 类是 Excel 附件读取服务的具体实现，
 * 它实现了 IAttachmentReaderService 接口，主要负责处理 Excel 附件的读取操作。
 * 该类提供了一个方法用于读取指定 Excel 附件的内容。
 */
public class ExcelReaderServiceImpl implements IAttachmentReaderService {

    /**
     * 此方法用于读取指定 ID 的 Excel 附件的内容。
     *
     * @param attachmentId 要读取的 Excel 附件的唯一标识符。
     * @return 返回读取到的 Excel 附件内容，如果读取失败或者没有内容则返回 null。
     */
    @Override
    public String readAttachment(String attachmentId) {
        return null;
    }
}