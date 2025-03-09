package com.alinesno.infra.smart.assistant.api;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用于表示麦克风数据的类，主要封装了与语音处理相关的参数和配置
 */
@Data
public class MicDataDto {

    /**
     * 表示当前操作类型，例如录音、播放等
     */
    private String act;

    /**
     * 具体操作类型的数据，例如录音文件、提示信息等
     */
    private ActData actData;

    /**
     * 用于表示具体操作类型数据的类，包括文件、提示信息和持续时间等
     */
    @Data
    public static class ActData {
        /**
         * 录音文件，使用MultipartFile类型以支持文件上传
         */
        private MultipartFile file;

        /**
         * 提示信息，用于指导用户进行特定操作
         */
        private String prompt;

        /**
         * 持续时间，表示录音的时长
         */
        private Double duration;
    }
}
