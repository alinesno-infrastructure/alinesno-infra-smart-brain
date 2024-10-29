package com.alinesno.infra.smart.assistant.role.llm;

import cn.hutool.core.util.IdUtil;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * 文本生成音频
 * https://help.aliyun.com/zh/dashscope/developer-reference/cosyvoice-quick-start?spm=a2c4g.11186623.0.0.3f927ff0UsYJvD
 *
 * 女声：longxiaochun
 * 男声：longshuo
 */
@Slf4j
@Service
public class QianWenAuditLLM {

    @Value("${alinesno.infra.smart.brain.qianwen.key:}")
    private String qianWenKey ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 生成音频
     *
     * @param voice
     * @param text
     *
     * @return mp3文件
     */
    public File generateAudit(String voice , String text ) {

        String model = "cosyvoice-v1";

        SpeechSynthesisParam param =
                SpeechSynthesisParam.builder()
                        .apiKey(qianWenKey)
                        .model(model)
                        .voice(voice)
                        .build();
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(param, null);
        ByteBuffer audio = synthesizer.call(text);

        log.debug("requestId: " + synthesizer.getLastRequestId());

        String mp3FileName = IdUtil.getSnowflakeNextId()  + ".mp3";
        File file = new File(localPath, mp3FileName) ;

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(audio.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file ;
    }

    /**
     * 生成图片
     *
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     */
    @SneakyThrows
    public List<Map<String, String>> generateImage(String prompt)  {
        return generateImage(prompt , ImageSynthesis.Models.WANX_V1);
    }

    /**
     * 生成图片
     * @param prompt
     * @param modelName
     * @return
     */
    @SneakyThrows
    public List<Map<String, String>> generateImage(String prompt , String modelName) {
        ImageSynthesis is = new ImageSynthesis();
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(qianWenKey)
                        .model(modelName)
                        .n(4)
                        .size("1024*1024")
                        .prompt(prompt)
                        .build();

        ImageSynthesisResult result = is.call(param);

        return result.getOutput().getResults() ;
    }

}