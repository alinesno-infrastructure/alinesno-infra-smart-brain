package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 文章图片附件
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/articleAttachment")
public class ArticleAvatarController {

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 文件上传
     * @return
     */
    @SneakyThrows
    @PostMapping("/upload")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, String type , String updateSupport){

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        R<String> r = storageConsumer.upload(targetFile) ;

        log.debug("ajaxResult= {}" , r);

        // 最后删除临时文件
        targetFile.delete() ;

        AjaxResult result =  AjaxResult.success("上传成功." , r.getData()) ;
        result.put("fileName" , newFileName);

        return result ;
    }

}
