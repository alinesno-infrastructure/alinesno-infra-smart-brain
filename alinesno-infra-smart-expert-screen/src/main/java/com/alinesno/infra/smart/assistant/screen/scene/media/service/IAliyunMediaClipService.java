package com.alinesno.infra.smart.assistant.screen.scene.media.service;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.smart.assistant.screen.core.dto.media.VideoBean;

/**
 * 媒体剪辑服务
 */
public interface IAliyunMediaClipService {

    /**
     * 生成全局口播模式
     */
    void generateGlobalKouBaoVideo(VideoBean bean);

    /**
     * 生成分组口播模式
     *
     * @return
     */
    JSONArray generateGroupKouBaoVideo(VideoBean bean);

}
