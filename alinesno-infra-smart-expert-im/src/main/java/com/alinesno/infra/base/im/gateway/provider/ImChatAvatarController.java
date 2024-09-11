package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/chat/")
public class ImChatAvatarController {

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 显示图片
     * @return
     */
    @GetMapping("/displayImage/{imageId}")
    public ResponseEntity<byte[]> displayImage(@PathVariable("imageId") String imageId){

        byte[] byteBody = null ;
        try{
            byteBody = storageConsumer.download(imageId , progress -> {}) ;
        }catch(Exception e){
            log.error("文件下载失败:{}" , e.getMessage());
            byteBody =  ResourceUtil.readBytes("default_avatar.jpeg") ;
        }

        return new ResponseEntity<>(byteBody, new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * 文件上传
     * @return
     */
    @SneakyThrows
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, String type , String updateSupport){

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        if("img".equals(type)){  // 图片上传类型
            AjaxResult ajaxResult = storageConsumer.upload(targetFile , "qiniu-kodo" , progress -> {
                System.out.println("total bytes: " + progress.getTotalBytes());
                System.out.println("current bytes: " + progress.getCurrentBytes());
                System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
            }) ;

            log.debug("ajaxResult= {}" , ajaxResult);
            return ajaxResult ;
        }

        return AjaxResult.success();
    }

}
