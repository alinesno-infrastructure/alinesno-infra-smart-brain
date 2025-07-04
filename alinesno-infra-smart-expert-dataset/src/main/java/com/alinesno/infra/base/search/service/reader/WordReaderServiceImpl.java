package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.spire.doc.Document;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * WordReaderServiceImpl 类是 Word 附件读取服务的具体实现，
 * 该类实现了 IAttachmentReaderService 接口，主要负责处理 Word 格式附件的读取操作。
 */
@Scope("prototype")
@Service
public class WordReaderServiceImpl  extends BaseReaderServiceImpl {

    /**
     * 此方法用于读取指定 ID 的 Word 附件内容。
     * 处理思路，解析出文档的菜单结构，返回每个章节的内容，然后再将每个章节内容整理成上下文会话
     *
     * @param attachmentDto 要读取的 Word 附件的唯一标识符。
     * @param uploadData
     * @return 返回读取到的 Word 附件内容，若读取失败或者附件无内容则返回 null。
     */
    @SneakyThrows
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData) {
        // 解析word文档的目录结构
        File file ;
        if(attachmentDto.getFile() != null){
           file = attachmentDto.getFile() ;
        }else{
            file = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());
        }

        //加载包含目录的Word文档
        Document doc = new Document();
        doc.loadFromFile(file.getAbsolutePath()) ;

        // 获取到所有内容，并去掉所有空格
        return cleanText(doc.getText());
    }

}