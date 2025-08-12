package com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools;

import cn.hutool.core.io.FileUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.document.parser.PdfBoxDocumentParser;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.adapter.SmartDocumentConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.common.utils.FileTool;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private SmartDocumentConsumer smartDocumentConsumer;

    // 定义每个拆分块的最大字符数
    private static final int MAX_CHUNK_SIZE = 5000;

    // 定义段落分隔正则表达式
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("(.+?(?:\n{2,}|$))", Pattern.DOTALL);

    /**
     * 解析文档并获取到分档内容分割列表
     * @param taskEntity
     * @return
     */
    @SneakyThrows
    public List<DocumentInfoBean> parseContent(DocReviewTaskEntity taskEntity) {
        List<DocumentInfoBean> documentInfoBeans = new ArrayList<>();

        File tempHtmlFile = FileTool.createTempHtmlFile(taskEntity.getHtmlContent());
        String content = smartDocumentConsumer.convertHtmlToPlainText(tempHtmlFile, true);
        splitContentIntelligently(content, documentInfoBeans);

        if(tempHtmlFile.exists()){
            tempHtmlFile.deleteOnExit();
        }
        return documentInfoBeans;
    }

    /**
     * 智能拆分纯文本内容
     * @param content 文档内容
     * @param documentInfoBeans 存储拆分后的文档信息列表
     */
    private void splitContentIntelligently(String content, List<DocumentInfoBean> documentInfoBeans) {
        // 先按段落分割
        String[] paragraphs = content.split("\n{2,}");

        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            // 如果当前块加上新段落会超过最大大小，先保存当前块
            if (currentChunk.length() + paragraph.length() > MAX_CHUNK_SIZE && !currentChunk.isEmpty()) {
                documentInfoBeans.add(new DocumentInfoBean(currentChunk.toString()));
                currentChunk.setLength(0);
            }

            currentChunk.append(paragraph).append("\n\n");
        }

        // 添加最后一个块
        if (!currentChunk.isEmpty()) {
            documentInfoBeans.add(new DocumentInfoBean(currentChunk.toString()));
        }
    }
}