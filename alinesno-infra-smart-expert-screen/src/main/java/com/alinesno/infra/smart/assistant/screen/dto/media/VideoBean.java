package com.alinesno.infra.smart.assistant.screen.dto.media;

import lombok.Data;
import lombok.ToString;

import java.util.List;

// 综合所有部分，最终的Bean可以是这样的：
@ToString
@Data
public class VideoBean {

    /**
     * 媒体内容
     */
    private List<MediaBean> mediaArray;

    /**
     * 背景音乐
     */
    private List<String> backgroundMusicArray ;

    /**
     * 背景图片
     */
    private List<String> backgroundImageArray ;

    /**
     * 生成的成片数
     */
    private int produceCount = 2;

    /**
     * 成片宽高，生成横屏文件
     */
    private int outputWidth = 1920;

    /**
     * 成片高，生成竖屏文件
     */
    private int outputHeight = 1080;

    /**
     * 标题列表，用于显示视频内容的标题。
     */
    private List<String> titleArray;

    /**
     * 语音文本列表，用于视频配音。
     * 注意：在分组口播模式下，此字段为 null，因为在这种模式下，语音文本不是预先定义的。
     */
    private List<String> speechTextArray; // This will be null for 分组口播模式

    /**
     * 检查是否启用了全局配音。
     *
     * @return 如果 speechTextArray 不为 null 且不为空，则返回 true，表示启用了全局配音；否则返回 false。
     */
    public boolean isGlobalVoiceover() {
        return speechTextArray != null && !speechTextArray.isEmpty();
    }

}