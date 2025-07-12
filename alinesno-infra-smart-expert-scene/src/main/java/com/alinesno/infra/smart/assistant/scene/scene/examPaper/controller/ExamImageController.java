package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 图片处理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/examImage")
public class ExamImageController {

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 文件上传
     * @return
     */
    @SneakyThrows
    @PostMapping("/uploadImage")
    public AjaxResult uploadImage(@RequestPart("file") MultipartFile file, String type , String updateSupport){

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        // 使用Hutool判断文件上传类型，只允许上传图片
        // 在获取fileType后添加验证
        String fileType = FileTypeUtil.getType(targetFile);
        if (!isAllowedImageType(fileType)) {
            FileUtils.deleteQuietly(targetFile); // 删除已上传的不合法文件
            return AjaxResult.error("只允许上传图片文件");
        }

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());
        R<String> r = storageConsumer.upload(targetFile) ;

        log.debug("ajaxResult= {}" , r);
        return AjaxResult.success("上传成功." , r.getData()) ;
    }

    // 添加允许的图片类型检查方法
    private boolean isAllowedImageType(String fileType) {
        Set<String> allowedTypes = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "bmp"));
        return allowedTypes.contains(fileType.toLowerCase());
    }
}
