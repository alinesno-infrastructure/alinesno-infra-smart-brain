package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.im.utils.ImageService;
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

@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/chat/")
public class ImChatAvatarController {

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    @Autowired
    private ImageService imageService ;

    /**
     * 处理图片远程加载的问题
     * @param imageId
     * @param request
     * @return
     */
    @GetMapping("/displayImage/{imageId}")
    @Async
    public CompletableFuture<ResponseEntity<byte[]>> displayImage(
            @PathVariable String imageId,
            HttpServletRequest request) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] byteBody = imageService.getImage(imageId);
                if (byteBody == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);

                // 设置协商缓存
                String tag = "\"" + DigestUtils.md5DigestAsHex(byteBody) + "\"";
                headers.setETag(tag);

                // 检查客户端缓存
                String ifNoneMatch = request.getHeader("If-None-Match");
                if (tag.equals(ifNoneMatch)) {
                    return new ResponseEntity<>(null, headers, HttpStatus.NOT_MODIFIED);
                }

                headers.setCacheControl("public, max-age=72000"); // 浏览器缓存24小时
                return new ResponseEntity<>(byteBody, headers, HttpStatus.OK);
            } catch (Exception e) {
                log.error("获取图片失败:{}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
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
            R<String> r = storageConsumer.upload(targetFile) ;

            log.debug("ajaxResult= {}" , r);
            return AjaxResult.success("上传成功." , r.getData()) ;
        }

        return AjaxResult.success();
    }

}
