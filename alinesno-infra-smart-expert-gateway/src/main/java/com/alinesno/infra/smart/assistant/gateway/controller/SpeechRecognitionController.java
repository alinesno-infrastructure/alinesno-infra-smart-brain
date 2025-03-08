package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.facade.response.AjaxResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Api(tags = "Speech Recognition")
@RestController
@RequestMapping("/api/infra/smart/assistant/speechRecognition")
public class SpeechRecognitionController {

    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult recognize(@RequestParam("audio") MultipartFile audioFile) {
        log.debug("语音识别请求已收到");

        try {
            // 临时存储上传的文件
            File tempFile = File.createTempFile("audio-", null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(audioFile.getBytes());
            }

            // 将音频文件转换为WAV格式并保存
            File wavFile = new File("output.wav");
            convertToWav(tempFile, wavFile);

            // 删除临时文件
            Files.deleteIfExists(tempFile.toPath());

            return AjaxResult.success("识别成功", "音频文件已转换并保存为WAV格式");
        } catch (IOException | EncoderException e) {
            log.error("处理音频文件时出错", e);
            return AjaxResult.error("处理音频文件时出错: " + e.getMessage());
        }
    }

    private void convertToWav(File inputFile, File outputFile) throws EncoderException {
        // 使用JAVE库进行音频转换
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le"); // 设置为未压缩的PCM编码

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("wav");
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(inputFile), outputFile, attrs);
    }
}