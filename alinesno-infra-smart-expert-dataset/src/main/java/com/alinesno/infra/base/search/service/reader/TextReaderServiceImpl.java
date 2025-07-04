package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * TextReaderServiceImpl 类是文本附件读取服务的具体实现，
 * 该类实现了 IAttachmentReaderService 接口，主要负责处理文本格式附件的读取操作。
 */
@Scope("prototype")
@Service
public class TextReaderServiceImpl  extends BaseReaderServiceImpl {

    /**
     * 此方法用于读取指定 ID 的文本附件内容。
     *
     * @param attachmentDto 要读取的文本附件的唯一标识符。
     * @param uploadData
     * @return 返回读取到的文本附件内容，若读取失败或无内容则返回 null。
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

        // 读取文件内容
        if (file != null) {
            String content = FileUtils.readFileToString(file, "UTF-8") ;
            return cleanText(content);
        }
        return null ;
    }
}