package com.alinesno.infra.smart.assistant.screen.dto.media;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 媒体信息实体类
 * 用于存储媒体相关信息，如名称、广告信息、媒体文件列表等
 */
@Data
public class MediaBean {

    /**
     * 媒体名称
     */
    private String name;

    /**
     * 商家广告信息
     * 用于存储与媒体相关的广告内容，以便在需要时展示
     */
    private String merchantAdvertisement;

    /**
     * 媒体文件数组
     * 存储一系列媒体文件的路径或标识符，便于管理和播放
     */
    private List<String> mediaArray;

    /**
     * 思想或心情
     * 用于表达媒体创作时的思考或创作者的心情
     */
    private String thought;

    /**
     * 媒体语音文本数组
     * 存储与媒体相关的语音内容文本，可用于字幕或语音识别
     */
    private List<String> mediaSpeechTextArray;

    /**
     * 描述
     * 对媒体内容的详细描述，提供更多的上下文信息
     */
    private String description;

    /**
     * 时长
     * 媒体内容的播放时长，用于表示视频、音频等媒体的长度
     */
    @Min(value = 1 , message = "时长不能小于1秒")
    @Max(value = 10 , message = "时长不能大于60秒")
    private int duration;

}

