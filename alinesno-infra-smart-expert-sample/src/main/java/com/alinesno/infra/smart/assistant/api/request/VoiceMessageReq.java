package com.alinesno.infra.smart.assistant.api.request;

import com.alinesno.infra.smart.assistant.api.wechat.VoiceMessage;

/**
 * 语音消息请求类
 * 该类继承自VoiceMessage，表示微信中的语音消息请求
 */
public class VoiceMessageReq extends VoiceMessage {

    private String Recognition  ;

    public String getRecognition() {
        return Recognition;
    }

    public void setRecognition(String recognition) {
        Recognition = recognition;
    }
}
