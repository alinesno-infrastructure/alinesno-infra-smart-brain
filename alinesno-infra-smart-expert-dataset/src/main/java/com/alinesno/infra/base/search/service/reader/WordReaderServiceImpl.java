package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.SneakyThrows;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;

/**
 * WordReaderServiceImpl 类是 Word 附件读取服务的具体实现，
 * 该类实现了 IAttachmentReaderService 接口，主要负责处理 Word 格式附件的读取操作。
 */
@Scope("prototype")
@Service
public class WordReaderServiceImpl extends BaseReaderServiceImpl {

    /**
     * 此方法用于读取指定 ID 的 Word 附件内容。
     * 处理思路，解析出文档的菜单结构，返回每个章节的内容，然后再将每个章节内容整理成上下文会话
     *
     * @param attachmentDto 要读取的 Word 附件的唯一标识符。
     * @param uploadData 上传数据配置
     * @return 返回读取到的 Word 附件内容，若读取失败或者附件无内容则返回 null。
     */
    @SneakyThrows
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData) {
        File file;
        if (attachmentDto.getFile() != null) {
            file = attachmentDto.getFile();
        } else {
            file = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            String content;

            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".docx")) {
                // 处理.docx文件
                XWPFDocument document = new XWPFDocument(fis);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                content = extractor.getText();
                extractor.close();
            } else if (fileName.endsWith(".doc")) {
                // 处理.doc文件
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                content = extractor.getText();
                extractor.close();
            } else {
                throw new IllegalArgumentException("不支持的文件格式: " + fileName);
            }

            return cleanText(content);
        }
    }
}