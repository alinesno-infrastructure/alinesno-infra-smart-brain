package com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools;

import cn.hutool.core.io.FileUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.document.parser.PdfBoxDocumentParser;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.adapter.SmartDocumentConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.bean.DocumentInfoBean;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 针对文档
 */
@Slf4j
@Service
public class ParserDocumentTool {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CloudStorageConsumer storageConsumer;

    @Autowired
    private SmartDocumentConsumer smartDocumentConsumer ;

    // 定义每个拆分块的最大字符数
    private static final int MAX_CHUNK_SIZE = 5000;

    /**
     * 解析文档并获取到分档内容分割列表
     * @param taskEntity
     * @return
     */
    @SneakyThrows
    public List<DocumentInfoBean> parseContent(DocReviewTaskEntity taskEntity) {

        List<DocumentInfoBean> documentInfoBeans = new ArrayList<>();

        String previewUrl = storageConsumer.getPreviewUrl(taskEntity.getDocumentId()).getData();
        String fileName = taskEntity.getDocumentName();

        log.debug("previewUrl= {}", previewUrl);

        // 直接下载文件内容
        byte[] fileBytes = storageConsumer.download(taskEntity.getDocumentId(), null);
        if (fileBytes == null || fileBytes.length == 0) {
            throw new RpcServiceRuntimeException("文件下载失败");
        }

        // 创建临时文件
        File tempFile = File.createTempFile("temp-", ".docx");
        Files.write(tempFile.toPath(), fileBytes);

        try (InputStream inputStream = new URL(previewUrl).openStream()) {
            fileBytes = IOUtils.toByteArray(inputStream);
            // 使用 fileBytes 进行后续处理
        }

        assert fileBytes != null;

        String fileType = FileUtil.getSuffix(fileName);

        File file = File.createTempFile("temp-file", "." + fileType);
        Files.write(file.toPath(), fileBytes);

        String content = smartDocumentConsumer.convertMarkdown(file) ;
        splitContent(content, documentInfoBeans);

        FileUtils.delete(tempFile);

        return documentInfoBeans;
    }

    /**
     * 拆分文档内容
     * @param content 文档内容
     * @param documentInfoBeans 存储拆分后的文档信息列表
     */
    private void splitContent(String content, List<DocumentInfoBean> documentInfoBeans) {
        int startIndex = 0;
        while (startIndex < content.length()) {
            int endIndex = Math.min(startIndex + MAX_CHUNK_SIZE, content.length());
            // 尽量在段落结束处拆分
            while (endIndex > startIndex && content.charAt(endIndex - 1) != '\n') {
                endIndex--;
            }
            // 如果没有找到段落结束符，按最大字符数拆分
            String chunk = content.substring(startIndex, endIndex);
            documentInfoBeans.add(new DocumentInfoBean(chunk));
            startIndex = endIndex;
        }
    }
}