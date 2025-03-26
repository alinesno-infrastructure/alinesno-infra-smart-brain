package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import org.springframework.stereotype.Service;

/**
 * CvsReaderServiceImpl 类是 CSV 附件读取服务的具体实现，
 * 它实现了 IAttachmentReaderService 接口，用于处理 CSV 附件的读取操作。
 * 该类提供了读取指定 CSV 附件内容的功能，具体实现逻辑由 readAttachment 方法完成。
 */
@Service
public class CvsReaderServiceImpl extends BaseReaderServiceImpl {

    /**
     * 此方法用于读取指定 ID 的 CSV 附件内容。
     *
     * @param attachmentDto 要读取的 CSV 附件的唯一标识符。
     * @param uploadData
     * @return 返回读取到的 CSV 附件内容，若读取失败或无内容则返回 null。
     */
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData) {
        return null;
    }
}