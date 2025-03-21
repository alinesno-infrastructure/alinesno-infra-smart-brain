package com.alinesno.infra.smart.assistant.gateway.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 语音识别控制器
 */
@Slf4j
@Api(tags = "Speech Recognition")
@RestController
@RequestMapping("/api/infra/smart/assistant/speechRecognition")
public class SpeechRecognitionController {

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Autowired
    protected CloudStorageConsumer cloudStorageConsumer;

    /**
     * 文件上传并识别
     * @param act
     * @param prompt
     * @param duration
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/recognizeFormData")
    public AjaxResult uploadFile(
            @RequestParam("roleId") String roleId ,

            @RequestParam("act") String act,
            @RequestParam("prompt") String prompt,
            @RequestParam("duration") String duration,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return AjaxResult.error("文件为空");
        }

        // 获取临时目录
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // 生成唯一的文件名
        String fileName = IdUtil.fastSimpleUUID() + "_" + file.getOriginalFilename();
        fileName = fileName.replaceFirst("[.][^.]+$", ".wav"); // 确保文件名为 .wav 格式
        Path filePath = tempDir.resolve(fileName);

        try {
            // 将 MultipartFile 转换为 File
            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);

            // 读取音频文件
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(tempFile);
            AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

            // 保存为 WAV 格式
            AudioSystem.write(audioInputStream, targetType, filePath.toFile());
            log.debug("文件保存路径: " + filePath);

            // 关闭音频流
            audioInputStream.close();

            // 删除临时文件
            tempFile.delete();
        } catch (Exception e) {
            log.error("文件转换为 WAV 格式失败" , e);
            return AjaxResult.error("文件转换为 WAV 格式失败: " + e.getMessage());
        }

        // 输出保存目录
        String saveDirectory = filePath.getParent().toString();
        System.out.println("文件保存目录: " + saveDirectory);

        // 打印其它参数
        log.debug("act: " + act);
        log.debug("prompt: " + prompt);
        log.debug("duration: " + duration);

        R<String> r = cloudStorageConsumer.uploadCallbackUrl(filePath.toFile().getAbsoluteFile() , "qiniu-kodo-pub");

        IndustryRoleEntity role = industryRoleService.getById(roleId) ;
        String recognitionText = llmModelService.speechRecognitionFile(role , r.getData()) ;

        return AjaxResult.success("识别成功" , recognitionText) ;
    }

    /**
     * 语音播放 speechRecognition
     */
    @SneakyThrows
    @PostMapping(value = "/playGenContent")
    public ResponseEntity<Resource> playGenContent(@RequestBody ChatMessageDto dto) {
        log.debug("语音播放请求已收到");

        IndustryRoleEntity role = industryRoleService.getById(dto.getRoleId()) ;
        String filePath = llmModelService.speechSynthesisFile(role , dto) ;

        log.debug("getSpeech filePath = {}" , filePath);

        try {
            // 本地录音文件路径
            File audioFile = new File(filePath) ;

            // 确保文件存在
            if (!audioFile.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            FileInputStream fileInputStream = new FileInputStream(audioFile);
            InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(audioFile.length());
            headers.setContentDispositionFormData("attachment", audioFile.getName());

            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}