package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.facade.response.AjaxResult;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

    // 新增的接收FormData数据的方法
    @SneakyThrows
    @PostMapping(value = "/recognizeFormData", consumes = "multipart/form-data")
    public AjaxResult recognizeFormData(
            @RequestPart("act") String act,
            @RequestPart("prompt") String prompt,
            @RequestPart("duration") Double duration,
            @RequestPart("file") MultipartFile file
    ) {
        log.debug("语音识别（FormData）请求已收到");

        // 获取系统临时目录
        String tempDir = System.getProperty("java.io.tmpdir");

        // 保存文件
        if (!file.isEmpty()) {
            File dest = new File(tempDir + File.separator + file.getOriginalFilename());

            log.debug("文件保存路径: " + dest.getAbsolutePath());

            file.transferTo(dest);
        }

        // 打印接收到的信息
        log.debug("act: " + act);
        log.debug("prompt: " + prompt);
        log.debug("duration: " + duration);
        log.debug("文件名: " + file.getOriginalFilename());

        return AjaxResult.success("语音识别成功");
    }

//    @SneakyThrows
//    @PostMapping(value = "/recognize")
//    public AjaxResult recognize(@RequestBody MicDataDto micData) {
//        log.debug("语音识别请求已收到");
//
//        MicDataDto.ActData actData = micData.getActData();
//        MultipartFile file = actData.getFile();
//        String prompt = actData.getPrompt();
//        Double duration = actData.getDuration();
//
//        // 获取系统临时目录
//        String tempDir = System.getProperty("java.io.tmpdir");
//
//        // 保存文件
//        if (!file.isEmpty()) {
//            File dest = new File(tempDir + File.separator + file.getOriginalFilename());
//            file.transferTo(dest);
//        }
//
//        // 这里可以进行其他信息的保存操作，例如保存到数据库等
//        // 为了示例简单，这里只是打印一下相关信息
//        log.debug("act: " + micData.getAct());
//        log.debug("prompt: " + prompt);
//        log.debug("duration: " + duration);
//
//        return AjaxResult.success("语音识别成功");
//    }

    /**
     * 语音播放 speechRecognition
     */
    @SneakyThrows
    @PostMapping(value = "/playGenContent")
    public ResponseEntity<Resource> playGenContent(@RequestBody ChatMessageDto dto) {
        log.debug("语音播放请求已收到");

        IndustryRoleEntity role = industryRoleService.getById(dto.getRoleId()) ;

        String filePath = llmModelService.speechRecognitionFile(role , dto) ;
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