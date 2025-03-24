package com.alinesno.infra.base.search.service.reader;

import cn.hutool.core.io.FileTypeUtil;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * BaseReaderServiceImpl
 */
@Slf4j
public abstract class BaseReaderServiceImpl implements IAttachmentReaderService {

    @Autowired
    private CloudStorageConsumer storageConsumer;

    /**
     * 根据附件ID获取文件
     * @param attachmentId
     * @return
     */
    protected File getFileById(Long attachmentId , String type) throws IOException {

        // 下载模板
        byte[] byteBody = storageConsumer.download(String.valueOf(attachmentId), progress -> {
            log.debug("progress: " + Math.round(progress.getRate() * 100) + "%");
        });

        String tempDir = System.getProperty("java.io.tmpdir");

        File sourceFile = new File(tempDir, attachmentId +"." + type) ;
        FileUtils.writeByteArrayToFile(sourceFile , byteBody);

        return sourceFile;
    }


    protected String cleanText(String text) {
        // 移除特定短语 'Free eBooks at Planet eBook.com' 及其周围的空白
        text = text.replaceAll("\\s*Free eBooks at Planet eBook\\.com\\s*", "");
        // 移除多余的空格
        text = text.replaceAll(" +", " ");
        // 移除 'David Copperfield' 之前或之后的不可打印字符
        text = text.replaceAll("(David Copperfield )?[\\x00-\\x1F]", "");
        // 将换行符替换为空格
        text = text.replace("\n", " ");
        // 移除连字符周围的空白
        text = text.replaceAll("\\s*-\\s*", "");
        return text;
    }

}
