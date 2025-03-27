package com.alinesno.infra.smart.assistant.screen.scene.media.controller;

import com.alinesno.infra.smart.assistant.screen.scene.media.service.IAliyunMediaClipService;
import com.alinesno.infra.smart.assistant.screen.core.dto.media.VideoBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发布视频
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/media")
public class MediaController {

    @Autowired
    private IAliyunMediaClipService mediaClipService;

    /**
     * 发布视频
     *
     * @param bean
     */
    @PostMapping("/publishVideo")
    public Object publishVideo(@RequestBody @Validated VideoBean bean) {

        log.debug("发布视频:{}", bean);

        if (bean.isGlobalVoiceover()) {
            mediaClipService.generateGlobalKouBaoVideo(bean);
        } else {
            return mediaClipService.generateGroupKouBaoVideo(bean);
        }

        return "生成成功";
    }

}
