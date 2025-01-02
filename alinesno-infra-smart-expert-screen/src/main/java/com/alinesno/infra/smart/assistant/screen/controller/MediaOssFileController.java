package com.alinesno.infra.smart.assistant.screen.controller;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.screen.properties.AliyunProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS文件处理
 */
@Slf4j
@RequestMapping("/api/infra/smart/assistant/mediaOss")
public class MediaOssFileController {

    private final OSS ossClient;
    private final AliyunProperties aliyunProperties;

    public MediaOssFileController(AliyunProperties aliyunProperties) {
        this.ossClient = new OSSClientBuilder().build(aliyunProperties.getEndpoint(), aliyunProperties.getAccessKeyId(), aliyunProperties.getAccessKeySecret());
        this.aliyunProperties = aliyunProperties;
    }

    /**
     * 文件上传到OSS，并指定文件夹
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "") String folder) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件为空");
            }

            // 确保文件夹路径以斜杠结尾
            if (!folder.isEmpty() && !folder.endsWith("/")) {
                folder += "/";
            }

            // 使用UUID生成唯一的文件名以避免冲突，并添加指定的文件夹路径
            String fileName = folder + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 上传文件到指定的Bucket和文件夹
            PutObjectResult result = ossClient.putObject(aliyunProperties.getBucketName(), fileName, file.getInputStream());
            log.info("文件上传成功：{}" , JSONObject.toJSONString(result));

            // 返回完整的文件路径作为响应信息
            return ResponseEntity.ok("文件上传成功：" + fileName);
        } catch (IOException e) {
            log.error("文件上传失败：" , e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败");
        }
    }

    /**
     * 创建文件夹（模拟）
     */
    @PostMapping("/createFolder")
    public ResponseEntity<String> createFolder(@RequestParam String folderName) {
        // 在OSS中，文件夹实际上是一个带有斜杠结尾的对象。
        String folderPath = folderName.endsWith("/") ? folderName : folderName + "/";
        try {
            // 上传一个空对象来模拟创建文件夹
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
            ossClient.putObject(aliyunProperties.getBucketName(), folderPath, inputStream);
            return ResponseEntity.ok("文件夹创建成功：" + folderName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件夹创建失败");
        }
    }

    /**
     * 删除文件或文件夹
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFileOrFolder(@RequestParam String key) {
        try {
            // 检查并删除指定的文件或文件夹（如果是一个文件夹，则需要递归删除所有内容）
            ossClient.deleteObject(aliyunProperties.getBucketName(), key);
            return ResponseEntity.ok("删除成功：" + key);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除失败");
        }
    }


    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String objectName) {
//        OSS ossClient = null;
        try {
            // 创建OSSClient实例。
            // ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

            // 获取Object。
            OSSObject ossObject = ossClient.getObject(aliyunProperties.getBucketName(), objectName);
            InputStream inputStream = ossObject.getObjectContent();

            // 将输入流转换为字节数组。
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] fileBytes = byteArrayOutputStream.toByteArray();

            // 设置响应头。
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", Base64.getEncoder().encodeToString(objectName.getBytes()));

            // 关闭资源。
            inputStream.close();
            byteArrayOutputStream.close();
            ossObject.close();

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("文件下载失败：" , e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    /**
     * 获取文件预览链接
     */
    @GetMapping("/preview")
    public ResponseEntity<String> getPreviewUrl(@RequestParam String objectName) {
//        OSS ossClient = null;
        try {
            // 创建OSSClient实例。
//            ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

            // 设置签名URL过期时间为1小时。
            Date expiration = new Date(new Date().getTime() + 3600 * 1000L);

            // 生成签名URL。
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    aliyunProperties.getBucketName(),
                    objectName,
                    com.aliyun.oss.HttpMethod.GET);

            request.setExpiration(expiration);

            URL signedUrl = ossClient.generatePresignedUrl(request);
            return new ResponseEntity<>(signedUrl.toString(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to generate preview URL", e);
            return new ResponseEntity<>("Failed to generate preview URL", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    // 确保在销毁Bean时关闭OSS客户端
    @PreDestroy
    public void cleanup() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}