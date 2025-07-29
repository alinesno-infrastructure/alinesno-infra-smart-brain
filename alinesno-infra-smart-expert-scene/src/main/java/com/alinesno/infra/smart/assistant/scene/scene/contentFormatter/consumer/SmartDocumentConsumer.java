package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.consumer;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * 智能文档消费者 - 用于与aip-base-office服务交互
 */
public class SmartDocumentConsumer {

    private final RestTemplate restTemplate;
    private String baseUrl;
    private String authToken;

    public SmartDocumentConsumer() {
        this.restTemplate = new RestTemplate();
        // 全局配置 RestTemplate 强制使用 UTF-8 解码
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    /**
     * 初始化配置
     * @param baseUrl 基础URL
     * @param authToken 认证Token
     */
    public void configure(String baseUrl, String authToken) {
        this.baseUrl = baseUrl;
        this.authToken = authToken;
    }

    /**
     * 检查配置是否有效
     */
    private void checkConfig() {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("Base URL is not configured");
        }
    }

    /**
     * 创建带认证的请求头（统一方法）
     */
    private HttpHeaders createHeaders(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (authToken != null && !authToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + authToken);
        }
        return headers;
    }

    /**
     * 统一文件上传请求方法（返回 String）
     */
    private ResponseEntity<String> postFileRequest(String endpoint, File file) {
        checkConfig();
        HttpHeaders headers = createHeaders(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(baseUrl + endpoint, HttpMethod.POST, requestEntity, String.class);
    }

    /**
     * 统一文件上传请求方法（返回 byte[]）
     */
    private ResponseEntity<byte[]> postFileRequestForBytes(String endpoint, File file) {
        checkConfig();
        HttpHeaders headers = createHeaders(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(baseUrl + endpoint, HttpMethod.POST, requestEntity, byte[].class);
    }

//    /**
//     * 统一文件上传请求方法（返回 String）
//     */
//    private ResponseEntity<String> postFileRequest(String endpoint, File file) {
//        checkConfig();
//        HttpHeaders headers = createHeaders(MediaType.MULTIPART_FORM_DATA);
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new FileSystemResource(file));
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        return restTemplate.exchange(baseUrl + endpoint, HttpMethod.POST, requestEntity, String.class);
//    }

    /**
     * 01_文档转换 - 获取文档目录结构
     * @param file 要处理的文件
     * @return API响应
     */
    public ResponseEntity<String> getDocumentToc(File file) {
        return postFileRequest("/api/v1/docx/toc", file);
    }

    /**
     * 01_文档转换 - DOCX转PDF
     * @param file 要处理的文件
     * @return API响应
     */
    public ResponseEntity<String> convertToPdf(File file) {
        return postFileRequest("/api/v1/docx/convertToPdf", file);
    }

//    /**
//     * 01_文档转换 - DOCX转HTML
//     * @param file 要处理的文件
//     * @return API响应
//     */
//    public ResponseEntity<String> convertToHtml(File file) {
//        return postFileRequest("/api/v1/docx/convertToHtml", file);
//    }

    /**
     * 01_文档转换 - DOCX转HTML
     * @param file 要处理的文件
     * @return API响应（UTF-8 编码的 HTML）
     */
    public ResponseEntity<String> convertToHtml(File file) {
        // 1. 发送请求并获取响应
        ResponseEntity<String> response = postFileRequest("/api/v1/docx/convertToHtml", file);

        // 2. 检查响应是否成功
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // 3. 确保响应头指定 UTF-8 编码
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "html", StandardCharsets.UTF_8));

            // 4. 返回修正后的响应（确保中文不乱码）
            return new ResponseEntity<>(
                    response.getBody(),
                    headers,
                    response.getStatusCode()
            );
        }
        return response;
    }

    /**
     * 01_文档转换 - 将HTML文档转换为DOCX格式
     * @param file 要处理的HTML文件
     * @return 包含DOCX文件字节数组的响应实体
     */
    public ResponseEntity<byte[]> convertHtmlToDocx(File file) {
        return postFileRequestForBytes("/api/v1/docx/convertHtmlToDocx", file);
    }

    /**
     * 01_文档转换 - 提取DOCX中的图片
     * @param file 要处理的文件
     * @return API响应
     */
    public ResponseEntity<String> extractImages(File file) {
        return postFileRequest("/api/v1/docx/extractImages", file);
    }

    /**
     * 01_文档转换 - 获取DOCX文档结构
     * @param file 要处理的文件
     * @return API响应
     */
    public ResponseEntity<String> getDocumentStructure(File file) {
        return postFileRequest("/api/v1/docx/structure", file);
    }

    /**
     * 01_文档转换 - 替换DOCX文本内容
     * @param file 要处理的文件
     * @return API响应
     */
    public ResponseEntity<String> replaceText(File file) {
        return postFileRequest("/api/v1/docx/replaceText", file);
    }

//    /**
//     * 01_文档转换 - 将HTML文档转换为DOCX格式
//     * @param file 要处理的HTML文件
//     * @return API响应
//     */
//    public ResponseEntity<String> convertHtmlToDocx(File file) {
//        return postFileRequest("/api/v1/docx/convertHtmlToDocx", file);
//    }

    /**
     * 02_文档格式化 - 政务公文格式化/排版化
     * @param file 要处理的文件
     * @return API响应
     */
    public ResponseEntity<byte[]> formatOfficialDocument(File file) {
        return postFileRequestForBytes("/api/v1/docx/format/official", file);
    }

//    /**
//     * 通用的文件上传请求方法
//     * @param endpoint API端点
//     * @param file 要上传的文件
//     * @return API响应
//     */
//    private ResponseEntity<String> postFileRequest(String endpoint, File file) {
//        if (baseUrl == null || baseUrl.isEmpty()) {
//            throw new IllegalStateException("Base URL is not configured");
//        }
//
//        HttpHeaders headers = createHeaders();
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new FileSystemResource(file));
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        String url = baseUrl + endpoint;
//        return restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                requestEntity,
//                String.class
//        );
//    }

//    /**
//     * 专门用于获取字节数组响应的文件上传请求方法
//     * @param endpoint API端点
//     * @param file 要上传的文件
//     * @return 包含字节数组的API响应
//     */
//    private ResponseEntity<byte[]> postFileRequestForBytes(String endpoint, File file) {
//        if (baseUrl == null || baseUrl.isEmpty()) {
//            throw new IllegalStateException("Base URL is not configured");
//        }
//
//        HttpHeaders headers = createHeaders();
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new FileSystemResource(file));
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        String url = baseUrl + endpoint;
//        return restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                requestEntity,
//                byte[].class
//        );
//    }
}