package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.facade.response.AjaxResult;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 语音识别控制器
 */
@Slf4j
@Api(tags = "Speech Recognition")
@RestController
@RequestMapping("/api/infra/smart/assistant/speechRecognition")
public class SpeechRecognitionController {

    // 新增的接收FormData数据的方法
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

}