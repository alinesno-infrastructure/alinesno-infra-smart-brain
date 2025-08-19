package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.dto.CommentData;
import com.dtflys.forest.annotation.*;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.List;

/**
 * 智能文档消费者 - 用于与aip-base-office服务交互(Forest版本)
 */
@BaseRequest(
        baseURL = "#{alinesno.infra.gateway.host}/base-office",
        connectTimeout = 30000,
        readTimeout = 60000
)
public interface SmartDocumentConsumer {

    /**
     * 文档转换成Markdown
     */
    @Post(url = "/api/v1/docx/convertToMarkdown")
    String convertMarkdown(@DataFile("file") File file);

    /**
     * 01_文档转换 - 获取文档目录结构
     * @param file 要处理的文件
     * @return API响应
     */
    @Post(url = "/api/v1/docx/toc")
    R<String> getDocumentToc(@DataFile("file") File file);

    /**
     * 01_文档转换 - DOCX转PDF
     * @param file 要处理的文件
     * @return API响应
     */
    @Post(url = "/api/v1/docx/convertToPdf")
    R<String> convertToPdf(@DataFile("file") File file);

    /**
     * 01_文档转换 - DOCX转HTML
     * @param file 要处理的文件
     * @return API响应（UTF-8 编码的 HTML）
     */
    @Post(url = "/api/v1/docx/convertToHtml")
    String convertToHtml(@DataFile("file") File file);

    /**
     * 01_文档转换 - 将HTML文档转换为DOCX格式
     * @param file 要处理的HTML文件
     * @return 包含DOCX文件字节数组的响应实体
     */
    @Post(url = "/api/v1/docx/convertHtmlToDocx")
    byte[] convertHtmlToDocx(@DataFile("file") File file);

    /**
     * 01_文档转换 - 提取DOCX中的图片
     * @param file 要处理的文件
     * @return API响应
     */
    @Post(url = "/api/v1/docx/extractImages")
    R<String> extractImages(@DataFile("file") File file);

    /**
     * 01_文档转换 - 获取DOCX文档结构
     * @param file 要处理的文件
     * @return API响应
     */
    @Post(url = "/api/v1/docx/structure")
    R<String> getDocumentStructure(@DataFile("file") File file);

    /**
     * 01_文档转换 - 替换DOCX文本内容
     * @param file 要处理的文件
     * @return API响应
     */
    @Post(url = "/api/v1/docx/replaceText")
    R<String> replaceText(@DataFile("file") File file);

    /**
     * 02_文档格式化 - 政务公文格式化/排版化
     * @param file 要处理的文件
     * @return API响应
     */
    @Post(url = "/api/v1/docx/format/official")
    byte[] formatOfficialDocument(@DataFile("file") File file);

    /**
     * HTML转政务公文
     * @param file HTML内容字符串
     * @return API响应
     */
    @Post(url = "/api/v1/docx/format/htmlToOfficial")
    String htmlToOfficial(@DataFile("file") File file);

    /**
     * Markdown转换成DOCX
     * @param markdownFile 上传的Markdown文件
     * @return 包含转换后DOCX文件的响应
     */
    @Post(url = "/api/v1/docx/markdownToDocx")
    byte[] markdownToDocx(@DataFile("file") File markdownFile);

    /**
     * 添加单条批注信息
     * @param file 上传的DOCX文件
     * @param selectedText 选中的文本
     * @param commentText 批注内容
     * @param author 批注作者
     * @return 包含添加批注后DOCX文件的响应
     */
    @Post(url = "/api/v1/docx/addComment", contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<byte[]> addComment(
            @DataFile("file") File file,
            @Query("selectedText") String selectedText,
            @Query("commentText") String commentText,
            @Query("author") String author);

    /**
     * 添加多个批注
     * @param file 上传的DOCX文件
     * @param comments 批注数据列表
     * @return 包含添加批注后DOCX文件的响应
     */
    @Post(url = "/api/v1/docx/addComments", contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<byte[]> addComments(
            @DataFile("file") File file,
            @Body List<CommentData> comments);

    /**
     * HTML转Markdown
     * @param file
     * @return
     */
    @Post(url = "/api/v1/docx/convertHtmlToMarkdown")
    String convertHtmlToMarkdown(@DataFile("file")File file , @Query("isFilterImg")boolean isFilterImg);

    /**
     * HTML转PlainTxt
     * @param file
     * @return
     */
    @Post(url = "/api/v1/docx/convertHtmlToPlainText")
    String convertHtmlToPlainText(@DataFile("file")File file , @Query("isFilterImg")boolean isFilterImg);
}